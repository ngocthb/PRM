package com.example.project.model;

import java.util.List;

public class OrderResponse {
    private int orderId;
    private int cartId;
    private int userId;
    private String paymentMethod;
    private String billingAddress;
    private String orderStatus;
    private String orderDate;
    private List<Object> payments;  // hoặc bạn có thể tạo class Payment riêng nếu cần
    private Object user;            // nếu bạn có model UserResponse thì thay Object -> UserResponse

    // Getters và Setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

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

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public List<Object> getPayments() {
        return payments;
    }

    public void setPayments(List<Object> payments) {
        this.payments = payments;
    }

    public Object getUser() {
        return user;
    }

    public void setUser(Object user) {
        this.user = user;
    }
}
