package com.example.project.model;

public class CreateOrderRequest {
    private int userId;
    private String paymentMethod;
    private String address;

    public CreateOrderRequest(int userId, String paymentMethod, String address) {
        this.userId = userId;
        this.paymentMethod = paymentMethod;
        this.address = address;
    }

    // Getter & Setter
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
