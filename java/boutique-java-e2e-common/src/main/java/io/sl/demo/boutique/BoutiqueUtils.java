package io.sl.demo.boutique;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;

@UtilityClass
@Slf4j
public class BoutiqueUtils {

    public static final String DEFAULT_BOUTIQUE_BASE_URL = "http://aa5174ca5746b43a39c0ddb2b2d1da06-1798784314.us-east-2.elb.amazonaws.com/";
    public static final int DEFAULT_TIMEOUT = 10_000;
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    public static final String BOUTIQUE_BASE_URL;

    static {
        BOUTIQUE_BASE_URL = getBoutiqueBaseUrlFromEnv();
    }

    private static CookieStore cookieStore = new BasicCookieStore();

    private static HttpClient getHttpClient() {

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(DEFAULT_TIMEOUT)
                .setSocketTimeout(DEFAULT_TIMEOUT)
                .setConnectionRequestTimeout(DEFAULT_TIMEOUT)
                .build();
        return HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .setDefaultCookieStore(cookieStore)
                .build();
    }

    public static String getResponseBodyString(HttpResponse httpResponse) throws IOException {
        return IOUtils.toString(httpResponse.getEntity().getContent(), Charset.defaultCharset());
    }

    public static HttpResponse sendGetRequest(String uri) throws IOException {
        log.debug("uri: {}", uri);
        HttpGet httpRequest = new HttpGet(uri);
        HttpResponse httpResponse = getHttpClient().execute(httpRequest);
        return httpResponse;
    }

    public static HttpResponse sendPostRequest(String uri, String requestBody) throws IOException {
        log.debug("uri: {}", uri);
        HttpPost httpRequest = new HttpPost(uri);
        HttpEntity httpEntity = new StringEntity(requestBody);
        httpRequest.setEntity(httpEntity);
        HttpClient httpClient = getHttpClient();
        HttpResponse httpResponse = httpClient.execute(httpRequest);
        return httpResponse;
    }

    public static String toJsonStr(Object obj) {
        return GSON.toJson(obj);
    }

    public static boolean isContainsDoctype(HttpResponse httpResponse) throws IOException {
        return BoutiqueUtils.getResponseBodyString(httpResponse).contains("DOCTYPE");
    }

    public static boolean assertResponseStatusCode(HttpResponse httpResponse, int expectedCode) {
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        log.debug("actual statusCode: {} expectedCode: {}", statusCode, expectedCode);
        return statusCode == expectedCode;
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

    public static void printMethodsForTesting(String clientObjectName) {
        StringBuilder sb = new StringBuilder("\n\n");
        for (Method method : BoutiqueClient.class.getMethods()) {
            if (!method.isAnnotationPresent(ForTesting.class)) {
                continue;
            }
            String methodName = method.getName();
            sb.append("@Test\n");
            sb.append("public void " + methodName + "Test() throws IOException{\n");
            sb.append("    " + clientObjectName + "." + methodName + "();");
            sb.append("\n}\n\n");
        }
        sb.append("\n\n");
        log.trace(sb.toString());
    }
}
