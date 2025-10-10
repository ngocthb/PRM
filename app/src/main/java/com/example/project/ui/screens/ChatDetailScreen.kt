package com.example.project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.project.model.ChatMessage
import androidx.navigation.NavController

private const val MY_USER_ID = 1

@Composable
fun ChatDetailScreen(chatId: Int, navController: NavController) {
    var sampleChatMessages by remember { mutableStateOf(
        listOf(
            ChatMessage(1, 1, "Hello!", "2025-09-26 10:00:00"),
            ChatMessage(2, 2, "Hi, how are you?", "2025-09-26 10:01:00"),
        )
    ) }

    var inputText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        ChatHeader(shopId= 1 ,contactName = "Dr. Alan C. Dalkin", isActive = true, contactImageUrl ="https://i.pravatar.cc/150?img=5" ,   navController = navController)
        // LazyColumn cuộn từ dưới lên
        LazyColumn(
            reverseLayout = true,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            items(sampleChatMessages.asReversed()) { chat ->
                ChatMessageItem(chat)
            }
        }

        // Input row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Type a message") },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .weight(1f)
                    .border(
                        width = 1.5.dp,
                        color = Color(0xFF6588E6),
                        shape = RoundedCornerShape(20.dp)
                    )
            )


            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        sampleChatMessages = sampleChatMessages + ChatMessage(
                            chatMessageID = sampleChatMessages.size + 1,
                            userID = MY_USER_ID,
                            message = inputText,
                            sentAt = "2025-09-30 22:00:00"
                        )
                        inputText = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFF6588E6), RoundedCornerShape(24.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Send",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun ChatMessageItem(chat: ChatMessage) {
    val isMe = chat.userID == MY_USER_ID

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = if (isMe) Color(0xFF6588E6) else Color(0xFFE5E5EA),
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isMe) 16.dp else 4.dp,
                        bottomEnd = if (isMe) 4.dp else 16.dp
                    )
                )
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                text = chat.message,
                color = if (isMe) Color.White else Color.Black
            )
        }
    }
}


@Composable
fun ChatHeader(
    shopId : Int,
    contactName: String,
    isActive: Boolean,
    contactImageUrl: String? = null,
    navController: NavController
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF6588E6))
            .padding(top = 32.dp, start = 12.dp, end = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            // Back button
            IconButton( onClick = { navController.popBackStack() },) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }



            // Contact Image
            if (contactImageUrl != null) {
                AsyncImage(
                    model = contactImageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.Gray, RoundedCornerShape(24.dp))
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Name + Status
            Column {
                Text(
                    text = contactName,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White // Nếu nền tối, text nên trắng
                )
                Text(
                    text = if (isActive) "Active now" else "Offline",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isActive) Color.Green else Color.LightGray
                )
            }
        }

        // Action Icons
        Row {
            IconButton(onClick = {   navController.navigate("shop_profile/${shopId}") }) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = "Info",
                    tint = Color.White
                )
            }
        }
    }
}

