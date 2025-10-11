package com.umtdg.pfo.tefas;

import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import com.umtdg.pfo.DateRange;
import com.umtdg.pfo.DateUtils;

public class TefasClient {
    static final String BASE_URL = "https://fundturkey.com.tr";
    static final String HISTORY_ENDPOINT = "api/DB/BindHistoryInfo";

    private RestClient client;

    private Logger logger = LoggerFactory.getLogger(TefasClient.class);

    public TefasClient()
        throws KeyManagementException,
            NoSuchAlgorithmException,
            KeyStoreException {
        SSLContext sslContext = SSLContextBuilder
            .create()
            .loadTrustMaterial(null, (chain, auto) -> true)
            .build();
        BasicCookieStore cookieStore = new BasicCookieStore();

        HttpHost proxy = null;
        PoolingHttpClientConnectionManager connectionManager = createConnectionManager(
            sslContext
        );
        CloseableHttpClient httpClient = createHttpClient(
            cookieStore,
            proxy,
            connectionManager
        );

        client = createRestClient(httpClient);

        client.get().retrieve().toBodilessEntity();
    }

    public List<TefasFund> fetchDateRange(LocalDate start, LocalDate end) {
        return fetchDateRange(new DateRange(start, end));
    }

    public List<TefasFund> fetchDateRange(DateRange fetchRange) {
        return DateUtils
            .splitDateRange(fetchRange)
            .parallelStream()
            .flatMap(range -> {
                logger
                    .trace("[{}] Fetching fund information from Tefas between", range);
                return fetch(new TefasFetchParams(range.getStart(), range.getEnd()))
                    .stream();
            })
            .toList();
    }

    private PoolingHttpClientConnectionManager createConnectionManager(
        SSLContext sslContext
    ) {
        PoolingHttpClientConnectionManagerBuilder builder = PoolingHttpClientConnectionManagerBuilder
            .create()
            .setTlsSocketStrategy(new DefaultClientTlsStrategy(sslContext))
            .setMaxConnTotal(10)
            .setMaxConnPerRoute(2);

        return builder.build();
    }

    private CloseableHttpClient createHttpClient(
        BasicCookieStore cookieStore, HttpHost proxy,
        HttpClientConnectionManager connectionManager
    ) {
        HttpClientBuilder builder = HttpClients
            .custom()
            .setDefaultCookieStore(cookieStore)
            .setConnectionManager(connectionManager);

        if (proxy != null) {
            builder = builder
                .setProxy(proxy)
                .setConnectionReuseStrategy((req, res, ctx) -> false)
                .setKeepAliveStrategy((res, ctx) -> TimeValue.ZERO_MILLISECONDS);
        }

        return builder.build();
    }

    private RestClient createRestClient(HttpClient httpClient) {
        // 10 minutes read and request timeouts are for cases where
        // Tefas takes a really long time to respond (i.e. ~3-5 minutes)
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(
            httpClient
        );
        requestFactory.setConnectTimeout(Duration.ofSeconds(3));
        requestFactory.setReadTimeout(Duration.ofMinutes(10));
        requestFactory.setConnectionRequestTimeout(Duration.ofMinutes(10));

        RestClient.Builder builder = RestClient
            .builder()
            .baseUrl(BASE_URL)
            .requestFactory(requestFactory);

        return builder.build();
    }

    private List<TefasFund> fetch(TefasFetchParams params) {
        ResponseEntity<TefasFetchResponse> res = client
            .post()
            .uri(HISTORY_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .acceptCharset(Charset.forName("UTF-8"))
            .header("Origin", BASE_URL)
            .body(params)
            .retrieve()
            .toEntity(TefasFetchResponse.class);

        HttpStatusCode statusCode = res.getStatusCode();
        if (!statusCode.is2xxSuccessful()) {
            logger.warn("Tefas returned status {}", statusCode);
            return List.of();
        }

        return res.getBody().getData();
    }
}
