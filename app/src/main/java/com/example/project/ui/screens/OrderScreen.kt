package com.example.project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.project.model.CartItemDto
import com.example.project.ui.viewmodel.CartViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.NumberFormat
import java.util.Locale
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    navController: NavHostController,
    viewModel: CartViewModel = viewModel()
) {
    val primaryColor = Color(0xFF6588E6)

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadCart()
    }

    Scaffold(
        containerColor = Color(0xFFFAFAFA),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Confirm Order", fontWeight = FontWeight.Bold, color = primaryColor)
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .padding(start = 4.dp) // padding nhẹ giữa nút và AppBar
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
                },
                modifier = Modifier.padding(start = 12.dp) // đây là cái giúp tránh sát mép thật
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // List of order items
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.items, key = { it.cartItemId }) { item ->
                    OrderItemRow(item)
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Totals
            val subtotal = uiState.totalPrice
            val tax = subtotal * 0.05
            val total = subtotal + tax

            Column {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Subtotal", color = Color.Gray)
                    Text("${NumberFormat.getNumberInstance(Locale.US).format(subtotal)}")
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tax (5%)", color = Color.Gray)
                    Text("${NumberFormat.getNumberInstance(Locale.US).format(tax)}")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total", fontWeight = FontWeight.Bold)
                    Text("${NumberFormat.getNumberInstance(Locale.US).format(total)}", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    // TODO: gọi API tạo order ở đây
                    navController.navigate("order_success")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Confirm Order", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun OrderItemRow(item: CartItemDto) {
    val primaryColor = Color(0xFF6588E6)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(item.productName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
            Text("${NumberFormat.getNumberInstance(Locale.US).format(item.price)} x ${item.quantity}",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }

        Text(
            "${NumberFormat.getNumberInstance(Locale.US).format(item.price * item.quantity)}",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = primaryColor
        )
    }
}

