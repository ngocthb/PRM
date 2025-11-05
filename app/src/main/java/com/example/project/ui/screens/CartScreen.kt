package com.example.project.ui.screens

import android.util.Log
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
import com.example.project.ui.viewmodel.CartViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project.api.ApiServices
import com.example.project.api.TokenManager
import com.example.project.model.CartItemDto
import com.example.project.model.CreateOrderRequest
import com.example.project.model.OrderRequest
import com.example.project.ui.components.SnackbarType
import com.example.project.ui.viewmodel.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CartScreen(navController: NavHostController, viewModel: CartViewModel = viewModel(), loginViewModel: LoginViewModel = viewModel(), scope: CoroutineScope, snackbarHostState: SnackbarHostState,) {
    val uiState by viewModel.uiState.collectAsState()
    val loginUiState by loginViewModel.uiState.collectAsState()
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
                            viewModel = viewModel
                        )

                    }
                }

                // Totals
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                ) {

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                        val total = uiState.totalPrice
                        Text("${NumberFormat.getNumberInstance(Locale.US).format(total)}",color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }

                // Checkout button
                val context = LocalContext.current
                val tokenManager = TokenManager.getInstance(context)
                val userId = tokenManager.getUserId().toInt()
                val user = loginUiState.user
                val payload = CreateOrderRequest(
                    userId,
                    "PayOS",
                    user?.address ?: ""
                )

                Button(
                    onClick = {
                        user?.address?.let {
                            viewModel.createOrder(
                                payload
                            ) { success, message, orderId  ->
                                println("✅ createOrder callback: success=$success, message=$message, orderId=$orderId")

                                if (success && orderId != null) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(message = "Create Order Successfully!", actionLabel = SnackbarType.SUCCESS.name)
                                    }

                                    println("➡️ Navigating to order/$orderId")
                                    navController.navigate("order/${orderId}")
                                } else {
                                    Toast.makeText(context, message ?: "Unknown error", Toast.LENGTH_SHORT).show()
                                }
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
    viewModel: CartViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {


        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(item.productName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(4.dp))
            Text("${NumberFormat.getNumberInstance(Locale.US).format(item.price)}", fontWeight = FontWeight.Bold , color = Color.Gray)
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Quantity controls
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Decrease
            Text("-", color = Color.Black , modifier = Modifier
                .clickable {
                    if (item.quantity > 1) {
                        viewModel.decreaseQuantity(item.productId)
                    } else {
                        viewModel.removeFromCart(item.productId)
                    }
                }
                .padding(horizontal = 6.dp, vertical = 4.dp)
            )
            Text(item.quantity.toString(),color = Color.Black, modifier = Modifier.padding(horizontal = 4.dp))
            // Increase
            Text("+",color = Color.Black, modifier = Modifier
                .clickable { viewModel.increaseQuantity(item.productId) }
                .padding(horizontal = 6.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Remove button
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Remove item",
            tint = Color.Red,
            modifier = Modifier
                .size(28.dp)
                .clickable { viewModel.removeFromCart(item.productId) }
        )
    }
}
