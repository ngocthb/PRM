package com.example.project.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalDensity

data class ProfileOption(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val onClick: () -> Unit
)

@Composable
fun ProfileScreen(navController: NavHostController) {
    val options = listOf(
        ProfileOption("Edit Profile", Icons.Outlined.Person, Color(0xFF4CAF50)) { /* TODO */ }, // xanh lá
        ProfileOption("Shopping Address", Icons.Outlined.LocationOn, Color(0xFFFF9800)) { /* TODO */ }, // cam
        ProfileOption("Wishlist", Icons.Outlined.Favorite, Color(0xFFF44336)) { /* TODO */ }, // đỏ
        ProfileOption("Order History", Icons.Outlined.List, Color(0xFF2196F3)) { /* TODO */ }, // xanh dương
        ProfileOption("Notification", Icons.Outlined.Notifications, Color(0xFFFFC107)) { /* TODO */ }, // vàng
        ProfileOption("Log Out", Icons.Outlined.Logout, Color(0xFFF44336)) { /* TODO */ } // tím
    )

    Scaffold(bottomBar = { BottomNavigationBar(navController) }) { padding ->
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {



// Avatar + Name + Status với nền trắng
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Box(contentAlignment = Alignment.Center) {
                // Vòng tròn nền
                Canvas(modifier = Modifier.size(140.dp)) {
                    val centerX = size.width / 2
                    val centerY = size.height / 2
                    val radii = listOf(50.dp, 65.dp, 80.dp).map { it.toPx() } // trong Canvas, toPx() có sẵn

                    radii.forEachIndexed { index, radius ->
                        drawCircle(
                            color = Color(0xFF6588E6).copy(alpha = 0.3f - index * 0.08f),
                            radius = radius,
                            center = androidx.compose.ui.geometry.Offset(centerX, centerY)
                        )
                    }
                }

                // Avatar
                Image(
                    painter = rememberAsyncImagePainter("https://i.pravatar.cc/150?img=5"),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .shadow(4.dp, CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Name and status
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Darlene Robertson", fontSize = 20.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color.Green)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Active status", fontSize = 14.sp, color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }




        // Options
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
    }}
}
