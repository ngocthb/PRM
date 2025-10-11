package com.example.project.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
import coil.compose.rememberAsyncImagePainter
import com.example.project.ui.screens.components.BottomNavigationBar
import androidx.compose.ui.platform.LocalContext
import com.example.project.utils.NotificationUtils

data class CartItem(
    val name: String,
    val category: String,
    val price: Double,
    val originalPrice: Double,
    val imageUrl: String,
    var quantity: Int,
    var isSelected: Boolean = true
)

@Composable
fun CartScreen(navController: NavHostController) {
    var cartItems by remember {
        mutableStateOf(
            listOf(
                CartItem("Tomato", "Vegetable", 18.0, 18.0, "https://i.pravatar.cc/150?img=1", 3),
                CartItem("Chocolate", "Bakery", 13.0, 15.0, "https://i.pravatar.cc/150?img=2", 7),
                CartItem("Avocado", "Fruit", 7.0, 10.0, "https://i.pravatar.cc/150?img=3", 2),
                CartItem("Milk", "Dairy", 15.0, 21.0, "https://i.pravatar.cc/150?img=4", 5)
            )
        )
    }

    val subtotal = cartItems.filter { it.isSelected }.sumOf { it.price * it.quantity }
    val tax = subtotal * 0.05
    val total = subtotal + tax
    val primaryColor = Color(0xFF6588E6)

    Scaffold(
        containerColor = Color(0xFFFAFAFA),
        bottomBar = { BottomNavigationBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = "My Cart",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // List of cart items
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                itemsIndexed(cartItems) { index, item ->
                    val context = LocalContext.current
                    CartItemRow(
                        item = item,
                        onQuantityChange = { newQty ->
                            cartItems = cartItems.toMutableList().also {
                                it[index] = it[index].copy(quantity = newQty)
                            }
                            val cartCount = cartItems.sumOf { it.quantity }
                            NotificationUtils.showCartBadgeNotification(context, cartCount)
                        },
                        onRemove = {
                            cartItems = cartItems.toMutableList().also { it.removeAt(index) }
                            val cartCount = cartItems.sumOf { it.quantity }
                            NotificationUtils.showCartBadgeNotification(context, cartCount)
                        },
                        onSelectChange = { isSelected ->
                            cartItems = cartItems.toMutableList().also {
                                it[index] = it[index].copy(isSelected = isSelected)
                            }
                            val cartCount = cartItems.filter { it.isSelected }.sumOf { it.quantity }
                        }
                    )
                }
            }

            // Totals
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Subtotal", color = Color.Gray)
                    Text("$${"%.2f".format(subtotal)}", fontWeight = FontWeight.Bold)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tax (5%)", color = Color.Gray)
                    Text("$${"%.2f".format(tax)}", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("$${"%.2f".format(total)}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }

            // Checkout button
            Button(
                onClick = { /* checkout */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    "Checkout",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit,
    onSelectChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = item.isSelected,
            onCheckedChange = { onSelectChange(it) }
        )

        Spacer(modifier = Modifier.width(12.dp))

        Image(
            painter = rememberAsyncImagePainter(item.imageUrl),
            contentDescription = item.name,
            modifier = Modifier.size(60.dp).clip(RoundedCornerShape(12.dp))
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(item.category, color = Color.Gray, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("$${item.price}", fontWeight = FontWeight.Bold)
                if (item.originalPrice > item.price) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "$${item.originalPrice}",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("-", modifier = Modifier
                .clickable { if (item.quantity > 1) onQuantityChange(item.quantity - 1) }
                .padding(horizontal = 6.dp, vertical = 4.dp))
            Text(item.quantity.toString(), modifier = Modifier.padding(horizontal = 4.dp))
            Text("+", modifier = Modifier
                .clickable { onQuantityChange(item.quantity + 1) }
                .padding(horizontal = 6.dp, vertical = 4.dp))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Remove item",
            tint = Color.Red,
            modifier = Modifier
                .size(28.dp)
                .clickable { onRemove() }
        )
    }
}


