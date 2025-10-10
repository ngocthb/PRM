package com.example.project.model

data class User(
    val userId: Int,
    val username: String,
    val email: String?,
    val phoneNumber: String?,
    val address: String?
)