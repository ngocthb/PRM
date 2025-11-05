package com.example.project.model

data class OrderRequest(
    val userId: Int,
    val paymentMethod: String,
    val address: String
)
