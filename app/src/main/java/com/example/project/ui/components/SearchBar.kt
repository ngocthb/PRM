package com.example.project.ui.screens.components
import androidx.compose.material3.TextFieldDefaults

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun SearchBar(
    onSearchChange: (String) -> Unit
) {
    var searchText by remember { mutableStateOf("") }

    OutlinedTextField(
        value = searchText,
        onValueChange = {
            searchText = it
            onSearchChange(it) // gọi callback mỗi khi người dùng nhập
        },
        shape = RoundedCornerShape(20.dp),
        placeholder = { Text("Search") },
        leadingIcon = {
            Icon(
                Icons.Filled.Search,
                contentDescription = "Search Icon",
                tint = Color(0xFF6588E6)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .border(
                width = 1.5.dp,
                color = Color(0xFF6588E6),
                shape = RoundedCornerShape(20.dp)
            ),

        singleLine = true,
        textStyle = TextStyle(color = Color.Black),
    )
}
