package com.example.project.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project.ui.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    navController: NavHostController,
    loginViewModel: LoginViewModel = viewModel()
) {
    val primaryColor = Color(0xFF6588E6)


    var isLoading by remember { mutableStateOf(true) }
    val loginUiState by loginViewModel.uiState.collectAsState()
    val user = loginUiState.user
    val userName = loginUiState.user?.username ?: "Guest"
    val firstLetter = userName.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    Scaffold(
        containerColor = Color(0xFFF6F8FF),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("My Profile", fontWeight = FontWeight.Bold, color = primaryColor)
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
                }
            )
        }
    ) { padding ->

            user?.let { u ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF6588E6)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = firstLetter,
                            color = Color.White,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = user.username ?: "--",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                    Text(
                        text = user.role ?: "--",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Thông tin chi tiết
                    ProfileInfoRow("Email", user.email ?: "--")
                    ProfileInfoRow("Phone Number", user.phoneNumber ?: "--")
                    ProfileInfoRow("Address", u.address ?: "--")

                    Spacer(modifier = Modifier.height(32.dp))



                }
            } ?: run {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Không tìm thấy người dùng!", color = Color.Gray)
                }
            }

    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(label, color = Color.Gray, fontSize = 13.sp)
        Text(value, color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}
