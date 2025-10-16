package com.example.project.ui.screens.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.maps.extension.style.expressions.dsl.generated.color

@Composable
fun TopBar(
    username: String?,
    address: String?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Hello, ${username ?: "Guest"}",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Text(
                text = address ?: "No address",
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        Icon(imageVector = Icons.Default.Person, contentDescription = "Profile", tint = Color.Black )
    }
}
