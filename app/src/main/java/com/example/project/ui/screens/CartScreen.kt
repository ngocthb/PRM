package com.example.project.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.project.ui.viewmodel.CartViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project.model.CartItemDto

@Composable
fun CartScreen(navController: NavHostController, viewModel: CartViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val primaryColor = Color(0xFF6588E6)

    LaunchedEffect(Unit) {
        viewModel.loadCart()
    }

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

            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = primaryColor)
                }
                return@Scaffold
            }

            if (uiState.items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Your cart is empty", color = Color.Gray)
                }
            } else {
                // List of cart items
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    // Use the items(items = ...) overload (imported above) so compiler doesn't pick the Int overload.
                    items(
                        items = uiState.items,
                        key = { it.cartItemId }
                    ) { item ->
                        CartItemRowNetwork(
                            item = item,
                            onQuantityChange = { newQty ->
                                viewModel.updateQuantity(item.cartItemId, newQty) { success, msg ->
                                    if (!success) Toast.makeText(context, msg ?: "Update failed", Toast.LENGTH_SHORT).show()
                                    else {
                                        val cartCount = viewModel.uiState.value.items.sumOf { it.quantity }
                                        NotificationUtils.showCartBadgeNotification(context, cartCount)
                                    }
                                }
                            },
                            onRemove = {
                                viewModel.removeItem(item.cartItemId) { success, msg ->
                                    if (!success) Toast.makeText(context, msg ?: "Delete failed", Toast.LENGTH_SHORT).show()
                                    else {
                                        val cartCount = viewModel.uiState.value.items.sumOf { it.quantity }
                                        NotificationUtils.showCartBadgeNotification(context, cartCount)
                                    }
                                }
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
                        val subtotal = uiState.totalPrice // assuming API returns totalPrice as subtotal; adjust if needed
                        Text("$${"%.2f".format(subtotal)}", fontWeight = FontWeight.Bold)
                    }
                    val tax = uiState.totalPrice * 0.05
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tax (5%)", color = Color.Gray)
                        Text("$${"%.2f".format(tax)}", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        val total = uiState.totalPrice + tax
                        Text("$${"%.2f".format(total)}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }

                // Checkout button
                Button(
                    onClick = {
                        viewModel.checkout { success, msg ->
                            if (success) {
                                Toast.makeText(context, "Checkout success", Toast.LENGTH_SHORT).show()
                                NotificationUtils.showCartBadgeNotification(context, 0)
                            } else {
                                Toast.makeText(context, msg ?: "Checkout failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
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
}

@Composable
fun CartItemRowNetwork(
    item: CartItemDto,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // You can add checkbox/select if you need
        Image(
            painter = rememberAsyncImagePainter("https://i.pravatar.cc/150?img=${(item.productId % 10) + 1}"),
            contentDescription = item.productName,
            modifier = Modifier.size(60.dp).clip(RoundedCornerShape(12.dp))
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(item.productName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("$${"%.2f".format(item.price)}", fontWeight = FontWeight.Bold)
                if (item.subTotal > item.price) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "$${"%.2f".format(item.subTotal)}",
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