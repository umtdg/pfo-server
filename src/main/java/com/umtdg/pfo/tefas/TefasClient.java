package com.umtdg.pfo.tefas;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.umtdg.pfo.DateRange;
import com.umtdg.pfo.exception.TefasSessionCreationException;

@Component
public class TefasClient {
    static final String BASE_URL = "https://www.tefas.gov.tr";
    static final String ENDPOINT_LIST = "/api/funds/fonGnlBlgSiraliGetir";
    static final String ENDPOINT_RETURNS = "/api/funds/fonGetiriBazliBilgiGetir";

    // The new API caps date ranges at one month per request.
    static final long RANGE_CHUNK_MONTHS = 1;
    // As high as we can go without getting throttled by TEFAS.
    static final long PAGE_SIZE = 10000L;

    // TEFAS' edge proxy rejects requests without a browser-like User-Agent with
    // "500 {"error":"Proxy request failed"}", so we spoof one on every request.
    static final String USER_AGENT =
        "Mozilla/5.0 (X11; Linux x86_64; rv:152.0) Gecko/20100101 Firefox/152.0";

    // TEFAS throttles bursts (429 ERR-224). Back off and retry on 429.
    static final int MAX_RETRIES = 6;
    static final long RETRY_BASE_BACKOFF_MS = 2000;

    private RestTemplate restTemplate;

    private Logger logger = LoggerFactory.getLogger(TefasClient.class);

    public TefasClient(RestTemplateBuilder restTemplateBuilder)
        throws TefasSessionCreationException {
        restTemplate = restTemplateBuilder.build();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            HttpHeaders headers = request.getHeaders();
            if (!headers.containsHeader(HttpHeaders.USER_AGENT)) {
                headers.set(HttpHeaders.USER_AGENT, USER_AGENT);
            }
            if (headers.getAccept().isEmpty()) {
                headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            }
            return execution.execute(request, body);
        });

        ResponseEntity<Void> response = restTemplate.getForEntity(BASE_URL, Void.class);

        HttpStatusCode status = response.getStatusCode();
        if (status.isError()) {
            throw new TefasSessionCreationException();
        }
    }

    public List<TefasFund> fetchDateRange(DateRange fetchRange) {
        // Fetch chunks sequentially: TEFAS throttles concurrent bursts, so a
        // parallelStream here reliably trips its rate limit (429 ERR-224).
        return fetchRange
            .split(RANGE_CHUNK_MONTHS)
            .stream()
            .flatMap(range -> {
                logger.trace("[{}] Fetching fund information from Tefas", range);
                return fetchDateRangeInternal(range);
            })
            .toList();
    }

    public List<TefasFundReturns> fetchReturns() {
        String url = String.format("%s%s", BASE_URL, ENDPOINT_RETURNS);
        TefasFundReturnsRequest body = new TefasFundReturnsRequest();

        logger.trace("Fetching fund returns from Tefas");
        TefasFundReturnsResponse response = postWithRetry(
            url, body, TefasFundReturnsResponse.class
        );

        return response.getResultList();
    }

    private Stream<TefasFund> fetchDateRangeInternal(DateRange fetchRange) {
        String url = String.format("%s%s", BASE_URL, ENDPOINT_LIST);

        List<TefasFund> funds = new ArrayList<>();
        long startIndex = 1;
        long totalCount = 1;

        while (startIndex <= totalCount) {
            TefasFundListRequest body = new TefasFundListRequest(
                fetchRange.getStart(), fetchRange.getEnd()
            );
            body.setStartIndex(startIndex);
            body.setEndIndex(startIndex + PAGE_SIZE - 1);

            logger.trace(
                "Fetching funds {} - {} of {}",
                body.getStartIndex(),
                body.getEndIndex(),
                totalCount
            );
            TefasFundListResponse response = postWithRetry(
                url, body, TefasFundListResponse.class
            );

            List<TefasFund> page = response.getResultList();
            funds.addAll(page);

            Integer responseTotal = response.getTotalCount();
            if (responseTotal != null) {
                totalCount = responseTotal;
            }

            // Guard against a short/empty page so we never loop forever.
            if (page.isEmpty()) {
                break;
            }

            startIndex += PAGE_SIZE;
        }

        return funds.stream();
    }

    // POST with exponential backoff on 429, honoring Retry-After when present.
    private <T> T postWithRetry(String url, Object body, Class<T> responseType) {
        long backoffMs = RETRY_BASE_BACKOFF_MS;
        for (int attempt = 1; ; attempt++) {
            try {
                return restTemplate.postForObject(url, body, responseType);
            } catch (HttpClientErrorException.TooManyRequests e) {
                if (attempt > MAX_RETRIES) {
                    throw e;
                }

                long waitMs = retryAfterMs(e, backoffMs);
                logger.warn(
                    "Throttled by Tefas (429); retry {}/{} after {} ms",
                    attempt,
                    MAX_RETRIES,
                    waitMs
                );

                sleep(waitMs);
                backoffMs *= 2;
            }
        }
    }

    private long retryAfterMs(HttpClientErrorException e, long fallbackMs) {
        HttpHeaders headers = e.getResponseHeaders();
        if (headers != null) {
            String retryAfter = headers.getFirst(HttpHeaders.RETRY_AFTER);
            if (retryAfter != null) {
                try {
                    // Retry-After is typically a delay in seconds.
                    return Long.parseLong(retryAfter.trim()) * 1000;
                } catch (NumberFormatException ignored) {
                    // Fall through to the exponential backoff below.
                }
            }
        }

        return fallbackMs;
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while backing off", e);
        }
    }
}
