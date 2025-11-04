package com.umtdg.pfo.tefas;

import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.umtdg.pfo.DateRange;
import com.umtdg.pfo.exception.TefasSessionCreationException;

@Component
public class TefasClient {
    static final String BASE_URL = "https://fundturkey.com.tr";
    static final String ENDPOINT_HISTORY = "/api/DB/BindHistoryInfo";

    private RestTemplate restTemplate;

    private Logger logger = LoggerFactory.getLogger(TefasClient.class);

    public TefasClient(RestTemplateBuilder restTemplateBuilder)
        throws TefasSessionCreationException {
        restTemplate = restTemplateBuilder.build();
        ResponseEntity<Void> response = restTemplate.getForEntity(BASE_URL, Void.class);

        HttpStatusCode status = response.getStatusCode();
        if (status.isError()) {
            throw new TefasSessionCreationException();
        }
    }

    public List<TefasFund> fetchDateRange(DateRange fetchRange) {
        return fetchRange
            .split()
            .parallelStream()
            .flatMap(range -> {
                logger.trace("[{}] Fetching fund information from Tefas", range);
                return fetchDateRangeInternal(range);
            })
            .toList();
    }

    private Stream<TefasFund> fetchDateRangeInternal(DateRange fetchRange) {
        String url = String.format("%s%s", BASE_URL, ENDPOINT_HISTORY);
        TefasFetchParams body = new TefasFetchParams(
            fetchRange.getStart(), fetchRange.getEnd()
        );

        TefasFetchResponse tefasResponse = this.restTemplate
            .postForObject(url, (Object) body, TefasFetchResponse.class);

        return tefasResponse.getData().stream();
    }
}
