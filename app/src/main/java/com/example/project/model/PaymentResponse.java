package com.example.project.model;

public class PaymentResponse {
    private String  checkoutUrl  ;

    public PaymentResponse(String checkoutUrl) {
        this.checkoutUrl = checkoutUrl;
    }

    public String getCheckoutUrl() {
        return checkoutUrl;
    }

    public void setCheckoutUrl(String checkoutUrl) {
        this.checkoutUrl = checkoutUrl;
    }
}
