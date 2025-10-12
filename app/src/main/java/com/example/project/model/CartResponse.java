package com.example.project.model;


import java.util.List;

public class CartResponse {
    private int cartId;
    private int userId;
    private double totalPrice;
    private String status;
    private List<CartItemDto> items;

    public CartResponse(int cartId, int userId, double totalPrice, String status, List<CartItemDto> items) {
        this.cartId = cartId;
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.status = status;
        this.items = items;
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

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<CartItemDto> getItems() {
        return items;
    }

    public void setItems(List<CartItemDto> items) {
        this.items = items;
    }
}
