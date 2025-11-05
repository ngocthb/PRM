package com.example.project.ui.components

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.example.project.api.ApiServices
import com.example.project.model.PaymentStatusResponse
import kotlinx.coroutines.*
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PaymentWebView(
    url: String,
    navController: NavHostController
) {
    val primaryColor = Color(0xFF6588E6)
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    var isChecking by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Payment", fontWeight = FontWeight.Bold, color = primaryColor)
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
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true

                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, pageUrl: String?) {
                            // ✅ Khi PayOS redirect về success URL
                            if (pageUrl?.contains("success", ignoreCase = true) == true && !isChecking) {
                                isChecking = true
                                scope.launch {
                                    checkPaymentStatus(context = context, navController = navController)
                                }
                            }
                        }
                    }
                    loadUrl(url)
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        )
    }
}

/**
 * ✅ Gọi API /api/Checkout/success để xác nhận thanh toán từ server
 */
suspend fun checkPaymentStatus(
    context: android.content.Context,
    navController: NavHostController
) {
    val api = ApiServices.getApiService(context)

    try {

        val response: Response<PaymentStatusResponse> = withContext(Dispatchers.IO) {
            api.checkPaymentSuccess().execute()
        }

        if (response.isSuccessful && response.body() != null) {
            // Bạn có thể chỉnh theo cấu trúc trả về của BE
            val json = response.body().toString()
            if (json.contains("SUCCESS", ignoreCase = true)) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Thanh toán thành công!", Toast.LENGTH_SHORT).show()
                    navController.navigate("profile") {
                        popUpTo("cart") { inclusive = true }
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Thanh toán chưa hoàn tất!", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Lỗi khi xác nhận thanh toán!", Toast.LENGTH_SHORT).show()
            }
        }
    } catch (e: Exception) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Lỗi mạng: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
