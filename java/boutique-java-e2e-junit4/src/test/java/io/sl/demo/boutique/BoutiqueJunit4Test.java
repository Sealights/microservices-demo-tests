package io.sl.demo.boutique;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

public class BoutiqueJunit4Test {
    private BoutiqueClientForJunit4 boutiqueClientForJunit4 = new BoutiqueClientForJunit4();

    @BeforeClass
    public static void before() {
        BoutiqueUtils.printMethodsForTesting("boutiqueClientForJunit4");
    }

    @Test
    public void requestOfMainPageShouldReturnHTMLTest() throws IOException {
        boutiqueClientForJunit4.requestOfMainPageShouldReturnHTML();
    }

    @Test
    public void itShouldCreateOrderTest() throws IOException {
        boutiqueClientForJunit4.itShouldCreateOrder();
    }

    @Test
    public void shouldNotFindProductTest() throws IOException {
        boutiqueClientForJunit4.shouldNotFindProduct();
    }

    @Test
    public void getProductPageTest() throws IOException {
        boutiqueClientForJunit4.getProductPage();
    }

    @Test
    public void requestCartTest() throws IOException {
        boutiqueClientForJunit4.requestCart();
    }

    @Test
    public void requestCartCheckoutTest() throws IOException {
        boutiqueClientForJunit4.requestCartCheckout();
    }

    @Test
    public void requestOfWrongPageShouldReturn404Test() throws IOException {
        boutiqueClientForJunit4.requestOfWrongPageShouldReturn404();
    }
}
