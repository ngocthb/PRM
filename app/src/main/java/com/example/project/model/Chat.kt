package com.example.project.model

data class Chat(
    val chatID: Int,
    val userName: String,
    val lastMessage: String,
    val lastSentAt: String,
    val avatarUrl : String
)