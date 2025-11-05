package com.example.project.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavHostController
import com.example.project.ui.screens.components.BottomNavigationBar
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project.api.TokenManager
import com.example.project.ui.viewmodel.LoginViewModel

data class ProfileOption(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val onClick: () -> Unit
)


@Composable
fun ProfileScreen(
    navController: NavHostController,
    onLogout: () -> Unit,
    loginViewModel: LoginViewModel = viewModel()
) {
    val context = LocalContext.current
    var showLogoutDialog by remember { mutableStateOf(false) }

    val loginUiState by loginViewModel.uiState.collectAsState()
    val options = listOf(
        ProfileOption("Profile", Icons.Outlined.Person, Color(0xFF4CAF50)) {
            navController.navigate("user")
        },

        ProfileOption("Order History", Icons.Outlined.List, Color(0xFF2196F3)) {navController.navigate("history") },
        ProfileOption("Log Out", Icons.Outlined.Logout, Color(0xFFF44336)) {
            showLogoutDialog = true
        }
    )

    // Confirmation dialog for logout
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    // Clear saved token & user id (ProfileScreen vẫn tự clear dữ liệu auth)
                    try {
                        TokenManager.getInstance(context).clear()
                    } catch (t: Throwable) {
                        // ignore if TokenManager not present; still proceed to call onLogout
                    }

                    // Gọi callback lên AppNavHost để xử lý điều hướng (AppNavHost truyền vào)
                    try {
                        onLogout()
                    } catch (t: Throwable) {
                        // ignore
                    }

                    Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                    showLogoutDialog = false
                }) {
                    Text("Log out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Confirm logout") },
            text = { Text("Are you sure you want to log out?") }
        )
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        containerColor = Color(0xFFFAFAFA),
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {

            val user = loginUiState.user?.username ?: "Guest"
            val firstLetter = user.firstOrNull()?.uppercaseChar()?.toString() ?: "?"

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                Box(contentAlignment = Alignment.Center) {
                    // concentric circles background
                    Canvas(modifier = Modifier.size(140.dp)) {
                        val centerX = size.width / 2
                        val centerY = size.height / 2
                        val radii = listOf(50.dp, 65.dp, 80.dp).map { it.toPx() }

                        radii.forEachIndexed { index, radius ->
                            drawCircle(
                                color = Color(0xFF6588E6).copy(alpha = 0.3f - index * 0.08f),
                                radius = radius,
                                center = androidx.compose.ui.geometry.Offset(centerX, centerY)
                            )
                        }
                    }


                    // Avatar image
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
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Name and status
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(user, fontSize = 20.sp, color = Color.Black)
                    Spacer(modifier = Modifier.height(4.dp))

                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Options list
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                options.forEach { option ->
                    val isLogout = option.title == "Log Out"
                    if (isLogout) {
                        Divider(color = Color.Gray.copy(alpha = 0.3f))
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { option.onClick() }
                            .padding(vertical = 24.dp)
                    ) {
                        Icon(
                            imageVector = option.icon,
                            contentDescription = option.title,
                            tint = option.color,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(option.title, fontSize = 16.sp, color = Color.Black, modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = Icons.Outlined.ChevronRight,
                            contentDescription = "Arrow",
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}