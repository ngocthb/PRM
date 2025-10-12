package com.example.project.model;

public class UpdateCartItemRequest {
    private  int quantity;
    private  int productId;

    public UpdateCartItemRequest(int quantity, int productId) {
        this.quantity = quantity;
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }
}
