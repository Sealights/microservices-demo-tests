package io.sl.demo.boutique;

import org.junit.Test;

import java.io.IOException;

public class BoutiqueJunit4Test {
    private BoutiqueClientForJunit4 boutiqueClientForJunit4 = new BoutiqueClientForJunit4();

    @Test
    public void boutiqueSuccessTest() throws IOException {
        boutiqueClientForJunit4.boutiqueSuccessTest();
    }

    @Test
    public void boutiqueFailTest() throws IOException {
        boutiqueClientForJunit4.boutiqueFailTest();
    }
}
