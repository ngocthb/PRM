package com.example.project.model;

public class PaymentRequest {
    private  int orderId;

    public PaymentRequest(int orderId) {
        this.orderId = orderId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
}
