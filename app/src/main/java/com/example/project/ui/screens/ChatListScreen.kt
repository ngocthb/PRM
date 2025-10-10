package com.example.project.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

import com.example.project.ui.screens.components.*
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.project.model.Chat
import com.example.project.ui.screens.components.BottomNavigationBar

@Composable
fun ChatListScreen(
    navController: NavHostController
) {
    val primaryColor = Color(0xFF6588E6)

    val recentChats = listOf(
        Chat(1, "Bella", "Typing...", "2:15", "https://i.pravatar.cc/150?img=1"),
        Chat(2, "Bee", "Ok, take care dear", "11:25", "https://i.pravatar.cc/150?img=2"),
        Chat(3, "Anne", "Where’re you?", "5:30", "https://i.pravatar.cc/150?img=3"),
        Chat(4, "Mommy", "Don’t forget to use your mask", "4:10", "https://i.pravatar.cc/150?img=4"),
    )

    val allChats = listOf(
        Chat(5, "Victoria", "I’m otw, wait for a few mins", "6:30", "https://i.pravatar.cc/150?img=5"),
        Chat(6, "Daddy", "I’ll be home tomorrow", "7:00", "https://i.pravatar.cc/150?img=6"),
    )

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding( vertical = 16.dp)
            ) {

                Text(
                    text = "Chat",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = primaryColor,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            SearchBar()

            // 🔹 Danh sách chat
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                item { SectionHeader("Recent Chats") }
                items(recentChats) { chat ->
                    ChatRow(chat) {
                        navController.navigate("chat_detail/${chat.chatID}")
                    }
                }

                item { SectionHeader("All Chats") }
                items(allChats) { chat ->
                    ChatRow(chat) {
                        navController.navigate("chat_detail/${chat.chatID}")
                    }
                }
            }
        }
    }
}


@Composable
fun SectionHeader(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.Black
        )

    }
}

@Composable
fun ChatRow(chat: Chat, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(chat.avatarUrl),
            contentDescription = chat.userName,
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = chat.userName,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )
            Text(
                text = chat.lastMessage,
                style = if (chat.lastMessage.lowercase().contains("typing"))
                    MaterialTheme.typography.bodyMedium.copy(color = Color.Gray, fontStyle = FontStyle.Italic)
                else
                    MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                maxLines = 1
            )
        }

        Text(
            text = chat.lastSentAt,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}
