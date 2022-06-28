package io.sl.demo.boutique;

public class BoutiqueClientForJunit4 extends BoutiqueClient {
    @Override
    String getTargetTestFramework() {
        return "JUnit4";
    }
}
