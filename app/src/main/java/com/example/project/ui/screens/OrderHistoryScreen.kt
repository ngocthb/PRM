package com.example.project.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.project.api.ApiServices
import com.example.project.api.TokenManager
import com.example.project.model.OrderHistory
import com.example.project.ui.viewmodel.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    navController: NavHostController,

) {
    val primaryColor = Color(0xFF6588E6)

    val scope = rememberCoroutineScope()

    var orders by remember { mutableStateOf<List<OrderHistory>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val tokenManager = TokenManager.getInstance(context)
    val userId = tokenManager.getUserId().toInt()


    LaunchedEffect(userId) {
        if (userId == null) {
            Toast.makeText(context, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
            return@LaunchedEffect
        }

        scope.launch(Dispatchers.IO) {
            try {
                val api = ApiServices.getApiService(context)
                val response = api.getOrderHistory().awaitResponse()
                if (response.isSuccessful && response.body() != null) {
                    orders = response.body()!!.filter { it.userId == userId }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Lỗi tải dữ liệu: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFFF6F8FF),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Order History", fontWeight = FontWeight.Bold, color = primaryColor) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = primaryColor)
                    }
                }
            )
        }
    ) { padding ->
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = primaryColor)
                }
            }
            orders.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text("No orders found", color = Color.Gray)
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(orders) { order ->
                        OrderHistoryItem(order)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderHistoryItem(order: OrderHistory) {
    val primaryColor = Color(0xFF6588E6)
    val statusColor = when (order.orderStatus.lowercase()) {
        "pending" -> Color(0xFFFFC107)
        "completed", "success" -> Color(0xFF4CAF50)
        "cancelled", "failed" -> Color(0xFFF44336)
        else -> Color.Gray
    }

    val dateFormatted = remember(order.orderDate) {
        try {
            val parsed = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(order.orderDate)
            SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(parsed!!)
        } catch (e: Exception) {
            order.orderDate
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text("Order #${order.orderId}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = primaryColor)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Payment: ${order.paymentMethod}", color = Color.DarkGray, fontSize = 14.sp)
        Text("Address: ${order.billingAddress}", color = Color.DarkGray, fontSize = 14.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Status: ", color = Color.DarkGray, fontSize = 14.sp)
            Text(order.orderStatus, color = statusColor, fontWeight = FontWeight.SemiBold)
        }
        Text("Date: $dateFormatted", color = Color.Gray, fontSize = 13.sp)
    }
}
