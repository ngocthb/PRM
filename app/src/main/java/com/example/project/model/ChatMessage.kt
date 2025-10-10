package com.example.project.model

data class ChatMessage(
    val chatMessageID: Int,
    val userID: Int,
    val message: String,
    val sentAt: String
)
