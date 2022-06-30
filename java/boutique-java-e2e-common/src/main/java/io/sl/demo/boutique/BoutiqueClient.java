package io.sl.demo.boutique;

import java.io.IOException;

public abstract class BoutiqueClient {

    private boolean printBody;
    private String boutiqueBaseUrl;

    public BoutiqueClient(String boutiqueBaseUrl) {
        this.boutiqueBaseUrl = boutiqueBaseUrl;
    }

    public BoutiqueClient() {
        this(BoutiqueUtils.BOUTIQUE_BASE_URL);
    }

    public void boutiqueSuccessTest() throws IOException {
        String body = BoutiqueUtils.sendRequestAndGetBody(boutiqueBaseUrl, printBody);
    }

    public void boutiqueFailTest() throws IOException {
        String body = BoutiqueUtils.sendRequestAndGetBody(boutiqueBaseUrl + "/fail", printBody);
    }

    public void setPrintBody(boolean printBody) {
        this.printBody = printBody;
    }

    abstract String getTargetTestFramework();
}
