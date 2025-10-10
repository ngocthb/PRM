package com.example.project.model

data class Order(
    val orderId: Int,
    val cartItems: List<CartItem>,
    val paymentMethod: String,
    val billingAddress: String,
    val orderStatus: String,
    val orderDate: String
)