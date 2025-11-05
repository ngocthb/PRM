package com.example.project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    navController: NavHostController,
    chatUserName: String = "Admin"
) {
    val primaryColor = Color(0xFF6588E6)
    val focusManager = LocalFocusManager.current

    // Danh sách tin nhắn
    var messages by remember {
        mutableStateOf(
            listOf(
                "Xin chào! Bạn cần hỗ trợ gì không?",
                "Cho tôi hỏi về đơn hàng của tôi nhé.",
                "Vâng, chúng tôi đang kiểm tra giúp bạn..."
            )
        )
    }
    var currentMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(chatUserName, fontWeight = FontWeight.Bold, color = primaryColor)
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Blue,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate("shop_map")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Map,
                            contentDescription = "Map",
                            tint = Color(0xFF6588E6),
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            )
        },
        containerColor = Color(0xFFF8F9FE)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 12.dp)
                // 👇 Khi chạm ra ngoài ô nhập thì ẩn bàn phím
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
        ) {
            // Danh sách tin nhắn
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { message ->
                    MessageBubble(
                        text = message,
                        isUser = message.contains("tôi") || message.contains("đơn hàng")
                    )
                }
            }

            // Nhập tin nhắn
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = currentMessage,
                    onValueChange = { currentMessage = it },
                    placeholder = { Text("Nhập tin nhắn...") },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(24.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (currentMessage.isNotBlank()) {
                            messages = messages + currentMessage
                            currentMessage = ""
                            focusManager.clearFocus() // 👈 Ẩn bàn phím sau khi gửi
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(primaryColor)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

/**
 * ✅ Tin nhắn (hiển thị trái/phải tùy người gửi)
 */
@Composable
fun MessageBubble(text: String, isUser: Boolean) {
    val bgColor = if (isUser) Color(0xFF6588E6) else Color.White
    val textColor = if (isUser) Color.White else Color.Black

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 260.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomEnd = if (isUser) 0.dp else 16.dp,
                        bottomStart = if (isUser) 16.dp else 0.dp
                    )
                )
                .background(bgColor)
                .padding(12.dp)
        ) {
            Text(text = text, color = textColor)
        }
    }
}
