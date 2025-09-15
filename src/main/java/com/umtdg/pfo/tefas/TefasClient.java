package com.umtdg.pfo.tefas;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.function.Consumer;

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
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestBodySpec;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TefasClient {
    static final String BASE_URL = "https://fundturkey.com.tr";
    static final String HISTORY_ENDPOINT = "api/DB/BindHistoryInfo";

    private RestClient client;

    private Logger logger = LoggerFactory.getLogger(TefasClient.class);

    public class CustomCharacterEscapes extends CharacterEscapes {
        @Override
        public int[] getEscapeCodesForAscii() {
            return standardAsciiEscapesForJSON();
        }

        @Override
        public SerializableString getEscapeSequence(int ch) {
            return null; // No custom escaping
        }
    }

    public TefasClient()
        throws KeyManagementException,
            NoSuchAlgorithmException,
            KeyStoreException {
        SSLContext sslContext = SSLContextBuilder
            .create()
            .loadTrustMaterial(null, (chain, auto) -> true)
            .build();
        BasicCookieStore cookieStore = new BasicCookieStore();

        HttpHost proxy = new HttpHost("127.0.0.1", 8090);
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

    public void fetchStreaming(TefasFetchParams params, Consumer<TefasFund> processor) {
        logger.info("Fetching fund information from TEFAS - streaming");

        RequestBodySpec req = client
            .post()
            .uri(HISTORY_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .acceptCharset(Charset.forName("UTF-8"))
            .header("Origin", BASE_URL)
            .body(params);

        req.exchange((clientReq, clientRes) -> {
            logger.trace("Processing Tefas response stream");

            HttpStatusCode statusCode = clientRes.getStatusCode();
            if (!statusCode.is2xxSuccessful()) {
                logger
                    .warn(
                        "Tefas returned {} as status code: {}",
                        statusCode,
                        clientRes.getBody()
                    );
                return null;
            }

            processJsonStream(clientRes.getBody(), processor);

            return null;
        });
    }

    private void processJsonStream(
        InputStream inputStream, Consumer<TefasFund> processor
    ) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonFactory factory = mapper.getFactory();
            JsonParser parser = factory.createParser(inputStream);

            while (parser.nextToken() != JsonToken.END_OBJECT) {
                if ("data".equals(parser.currentName())) {
                    parser.nextToken();

                    while (parser.nextToken() != JsonToken.END_ARRAY) {
                        if (parser.currentToken() == JsonToken.START_OBJECT) {
                            TefasFund fund = mapper.readValue(parser, TefasFund.class);
                            processor.accept(fund);
                        }
                    }
                    break;
                }
            }
        } catch (IOException exc) {
            throw new RuntimeException(
                "IOException - Tefas response JSON streaming failed", exc
            );
        } catch (Exception exc) {
            throw new RuntimeException(
                "Unknown - Tefas response JSON streaming failed", exc
            );
        }
    }

    private PoolingHttpClientConnectionManager createConnectionManager(
        SSLContext sslContext
    ) {
        PoolingHttpClientConnectionManagerBuilder builder = PoolingHttpClientConnectionManagerBuilder
            .create()
            .setTlsSocketStrategy(new DefaultClientTlsStrategy(sslContext));

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
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        requestFactory.setConnectTimeout(Duration.ofSeconds(3));
        requestFactory.setReadTimeout(Duration.ofMinutes(10));
        requestFactory.setConnectionRequestTimeout(Duration.ofMinutes(10));

        RestClient.Builder builder = RestClient
            .builder()
            .baseUrl(BASE_URL)
            .requestFactory(requestFactory);

        return builder.build();
    }
}
