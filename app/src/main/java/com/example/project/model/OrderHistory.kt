package com.example.project.model

data class OrderHistory(
    val orderId: Int,
    val cartId: Int,
    val userId: Int,
    val paymentMethod: String,
    val billingAddress: String,
    val orderStatus: String,
    val orderDate: String
)
