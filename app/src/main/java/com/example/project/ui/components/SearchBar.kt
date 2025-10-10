package com.example.project.ui.screens.components

import androidx.compose.foundation.border
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.graphics.Color


@Composable
fun SearchBar() {
    OutlinedTextField(
        value = "",
        onValueChange = {},
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
            .border(width = 1.5.dp, color = Color(0xFF6588E6), shape = RoundedCornerShape(20.dp)),
        singleLine = true,
    )

}
