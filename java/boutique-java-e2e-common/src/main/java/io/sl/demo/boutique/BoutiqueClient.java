package io.sl.demo.boutique;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.sl.demo.boutique.BoutiqueUtils.assertResponseStatusCode;
import static io.sl.demo.boutique.BoutiqueUtils.isContainsDoctype;
import static io.sl.demo.boutique.BoutiqueUtils.sendGetRequest;
import static io.sl.demo.boutique.BoutiqueUtils.sendPostRequest;

@Slf4j
public abstract class BoutiqueClient {

    private String boutiqueBaseUrl;
    private static final List<String> PRODUCTS = Collections.unmodifiableList(
            Arrays.asList(
                    "0PUK6V6EV0",
                    "1YMWWN1N4O",
                    "2ZYFJ3GM2N",
                    "66VCHSJNUP",
                    "6E92ZMYYFZ",
                    "9SIQT8TOJO",
                    "L9ECAV7KIM",
                    "LS4PSXUNUM",
                    "OLJCESPC7Z"
            )
    );
    private static final String END1_ROOT_EP = "/";
    private static final String END2_CART_EP = "/cart";
    private static final String END3_CART_CHECKOUT_EP = "/cart/checkout";
    private static final String END4_NON_EXISTING_PRODUCT = "/product/1YMWWN1N4O111122222";
    private static final String END5_INCORRECT_EP = "/incorrectroute";
    private static final String END6_EXISTING_PRODUCT = PRODUCTS.get(8);
    private static final String END7_PRODUCT_EP = "/product/";

    protected BoutiqueClient(String boutiqueBaseUrl) {
        this.boutiqueBaseUrl = boutiqueBaseUrl;
    }

    protected BoutiqueClient() {
        this(BoutiqueUtils.BOUTIQUE_BASE_URL);
    }

    public static String getEndpointUri(String baseUrl, String... parts) {
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            sb.append(part).append("/");
        }
        return baseUrl.replaceAll("/$", "") + sb.toString()
                .replaceAll("/+", "/")
                .replaceAll("/$", "");
    }

    @ForTesting
    public void requestOfMainPageShouldReturnHTML() throws IOException {
        String uri = getEndpointUri(boutiqueBaseUrl, END1_ROOT_EP);
        HttpResponse httpResponse = sendGetRequest(uri);
        assert assertResponseStatusCode(httpResponse, 200);
        assert isContainsDoctype(httpResponse);
    }

    @ForTesting
    public void getProductPage() throws IOException {
        String uri = getEndpointUri(boutiqueBaseUrl, END1_ROOT_EP, END7_PRODUCT_EP, END6_EXISTING_PRODUCT);
        HttpResponse httpResponse = sendGetRequest(uri);
        assert assertResponseStatusCode(httpResponse, 200);
        assert isContainsDoctype(httpResponse);
    }


    @ForTesting
    public void itShouldCreateOrder() throws IOException {
        sendGetRequest(getEndpointUri(boutiqueBaseUrl, END7_PRODUCT_EP, END6_EXISTING_PRODUCT));
        String uri = getEndpointUri(boutiqueBaseUrl, END3_CART_CHECKOUT_EP);
        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("email", "someone@example.com");
        requestBodyMap.put("street_address", "1600 Amphitheatre Parkway");
        requestBodyMap.put("zip_code", "94043");
        requestBodyMap.put("city", "Mountain View");
        requestBodyMap.put("state", "CA");
        requestBodyMap.put("country", "United States");
        requestBodyMap.put("credit_card_number", "4432-8015-6152-0454");
        requestBodyMap.put("credit_card_expiration_month", "1");
        requestBodyMap.put("credit_card_expiration_year", "2039");
        requestBodyMap.put("credit_card_cvv", "672");

        String requestBody = BoutiqueUtils.toJsonStr(requestBodyMap);
        HttpResponse httpResponse = BoutiqueUtils.sendPostRequest(uri, requestBody);
        assert assertResponseStatusCode(httpResponse, 500);
        assert isContainsDoctype(httpResponse);
    }

    @ForTesting
    public void shouldNotFindProduct() throws IOException {
        String url = getEndpointUri(boutiqueBaseUrl, END4_NON_EXISTING_PRODUCT);
        HttpResponse httpResponse = sendGetRequest(url);
        assert assertResponseStatusCode(httpResponse, 500);
        assert BoutiqueUtils.getResponseBodyString(httpResponse).contains("Internal Server Error");
    }

    @ForTesting
    public void requestOfWrongPageShouldReturn404() throws IOException {
        String url = getEndpointUri(boutiqueBaseUrl, END5_INCORRECT_EP);
        HttpResponse httpResponse = sendGetRequest(url);
        assert assertResponseStatusCode(httpResponse, 404);
        assert BoutiqueUtils.getResponseBodyString(httpResponse).contains("404 page not found");
    }

    @ForTesting
    public void requestCart() throws IOException {
        String url = getEndpointUri(boutiqueBaseUrl, END2_CART_EP);
        HttpResponse httpResponse = sendGetRequest(url);
        assert assertResponseStatusCode(httpResponse, 200);
        assert isContainsDoctype(httpResponse);
    }

    @ForTesting
    public void requestCartCheckout() throws IOException {
        String url = getEndpointUri(boutiqueBaseUrl, END3_CART_CHECKOUT_EP);
        HttpResponse httpResponse = sendPostRequest(url, "{}");
        assert assertResponseStatusCode(httpResponse, 500);
        assert isContainsDoctype(httpResponse);
    }

    abstract String getTargetTestFramework();
}
