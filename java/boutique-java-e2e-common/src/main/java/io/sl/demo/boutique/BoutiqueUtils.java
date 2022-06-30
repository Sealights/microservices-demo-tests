package io.sl.demo.boutique;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.nio.charset.Charset;

@UtilityClass
@Slf4j
public class BoutiqueUtils {

    public static final String DEFAULT_BOUTIQUE_BASE_URL = "http://boutique.dev.sealights.co:8080/";
    public static final int DEFAULT_TIMEOUT = 10_000;
    private static final HttpClient HTTP_CLIENT;
    public static final String BOUTIQUE_BASE_URL;

    static {
        HTTP_CLIENT = getHttpClient();
        BOUTIQUE_BASE_URL = getBoutiqueBaseUrlFromEnv();
    }

    private static HttpClient getHttpClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(DEFAULT_TIMEOUT)
                .setSocketTimeout(DEFAULT_TIMEOUT)
                .setConnectionRequestTimeout(DEFAULT_TIMEOUT)
                .build();
        HttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .build();
        return httpClient;
    }

    public static String sendRequestAndGetBody(String uri, boolean printBody) throws IOException {
        HttpUriRequest httpRequest = new HttpGet(uri);
        HttpResponse httpResponse = HTTP_CLIENT.execute(httpRequest);
        String body = IOUtils.toString(httpResponse.getEntity().getContent(), Charset.defaultCharset());
        if (printBody) {
            System.out.println(body);
        }
        return body;
    }

    public static String getBoutiqueBaseUrlFromEnv() {
        String result;
        String testEndpointEnvValue = System.getenv("TEST_ENDPOINT");
        String testBoutiqueEndpointEnvValue = System.getenv("TEST_BOUTIQUE_ENDPOINT");
        if (testBoutiqueEndpointEnvValue != null && !testBoutiqueEndpointEnvValue.isEmpty()) {
            result = testBoutiqueEndpointEnvValue;
        } else if (testEndpointEnvValue != null && !testEndpointEnvValue.isEmpty()) {
            result = testEndpointEnvValue;
        } else {
            result = DEFAULT_BOUTIQUE_BASE_URL;
        }
        log.info("getBoutiqueBaseUrlFromEnv result: {}", result);
        return result;
    }
}
