package com.example.project.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.project.ui.screens.components.BottomNavigationBar

data class CartItem(
    val name: String,
    val category: String,
    val price: Double,
    val originalPrice: Double,
    val imageUrl: String,
    var quantity: Int,
    var isSelected: Boolean = true // mới: để lưu trạng thái tick
)

@Composable
fun CartScreen(navController: NavHostController) {
    var cartItems by remember {
        mutableStateOf(
            listOf(
                CartItem("Tomato", "Vegetable", 18.0, 18.0, "https://i.pravatar.cc/150?img=1", 3),
                CartItem("Chocolate", "Bakery", 13.0, 15.0, "https://i.pravatar.cc/150?img=2", 7),
                CartItem("Avocado", "Fruit", 7.0, 10.0, "https://i.pravatar.cc/150?img=3", 2),
                CartItem("Milk", "Dairy", 15.0, 21.0, "https://i.pravatar.cc/150?img=4", 5),
                CartItem("Milk", "Dairy", 15.0, 21.0, "https://i.pravatar.cc/150?img=4", 5),
                CartItem("Milk", "Dairy", 15.0, 21.0, "https://i.pravatar.cc/150?img=4", 5),
                CartItem("Milk", "Dairy", 15.0, 21.0, "https://i.pravatar.cc/150?img=4", 5),
                CartItem("Milk", "Dairy", 15.0, 21.0, "https://i.pravatar.cc/150?img=4", 5)

            )
        )
    }

    // Chỉ tính tổng dựa trên các item được chọn
    val subtotal = cartItems.filter { it.isSelected }.sumOf { it.price * it.quantity }
    val tax = subtotal * 0.05
    val total = subtotal + tax

    Scaffold(bottomBar = { BottomNavigationBar(navController) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            ) {
                Text(
                    text = "My Cart",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF6588E6),
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(cartItems) { index, item ->
                    CartItemRow(
                        item,
                        onQuantityChange = { newQty ->
                            cartItems = cartItems.toMutableList().also { it[index] = it[index].copy(quantity = newQty) }
                        },
                        onRemove = {
                            cartItems = cartItems.toMutableList().also { it.removeAt(index) }
                        },
                        onSelectChange = { isSelected ->
                            cartItems = cartItems.toMutableList().also { it[index] = it[index].copy(isSelected = isSelected) }
                        }
                    )
                }
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Subtotal", color = Color.Gray)
                    Text("$${"%.2f".format(subtotal)}")
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tax (5%)", color = Color.Gray)
                    Text("$${"%.2f".format(tax)}")
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total", fontWeight = FontWeight.Bold)
                    Text("$${"%.2f".format(total)}", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { /* handle checkout */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 110.dp, start = 12.dp, end = 12.dp)
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6588E6)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Checkout", color = Color.White, style = MaterialTheme.typography.titleMedium)
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
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Checkbox để chọn item
        Checkbox(
            checked = item.isSelected,
            onCheckedChange = { onSelectChange(it) }
        )

        Spacer(modifier = Modifier.width(8.dp))

        Image(
            painter = rememberAsyncImagePainter(item.imageUrl),
            contentDescription = item.name,
            modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp))
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, fontWeight = FontWeight.Bold)
            Text(item.category, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("$${item.price}", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(4.dp))
                if (item.originalPrice > item.price)
                    Text("$${item.originalPrice}", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("-", modifier = Modifier
                .clickable { if (item.quantity > 0) onQuantityChange(item.quantity - 1) }
                .padding(8.dp))
            Text(item.quantity.toString(), modifier = Modifier.padding(horizontal = 4.dp))
            Text("+", modifier = Modifier
                .clickable { onQuantityChange(item.quantity + 1) }
                .padding(8.dp))
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Remove item",
            tint = Color.Red,
            modifier = Modifier
                .size(24.dp)
                .clickable { onRemove() }
        )
    }
}
