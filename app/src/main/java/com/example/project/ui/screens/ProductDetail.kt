package com.example.project.ui.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete // Changed from Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration // For screen height
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource // Keep this if you add placeholder/error drawables
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.project.R // Assuming you have R.drawable.placeholder_image etc.
import com.example.project.model.Product
import com.example.project.model.ProductDetailResponse
import com.example.project.ui.viewmodel.CartViewModel
import com.example.project.ui.viewmodel.ProductViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale


val AccentBlue = Color(0xFF6588E6)
val SubtleGray = Color(0xFF888888)
val LightSurface = Color(0xFFF5F5F5)
@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun ProductDetail(
    productId: Int,
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    cartViewModel: CartViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    viewModel: ProductViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val detailState by viewModel.detailState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Khi screen khởi tạo, gọi API lấy product detail
    LaunchedEffect(productId) {
        viewModel.getProductDetail(productId)
    }

    val product = detailState.product
    if (product == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    var quantity by remember { mutableStateOf(1) }

    val onAddToCart = {
        cartViewModel.addToCart(product.productId, quantity) { success, message ->
            scope.launch {
                message?.let {
                    snackbarHostState.showSnackbar(
                        message = it,
                        actionLabel = if (success) "SUCCESS" else "ERROR"
                    )
                }
            }
        }
    }

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Scaffold(containerColor = Color(0xFFFAFAFA)) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            // --- UI vẫn nguyên như trước ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight * 1 / 2)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = product.imageUrl),
                    contentDescription = product.productName,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)),
                    contentScale = ContentScale.Crop
                )

                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 16.dp, top = 32.dp)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), CircleShape)
                        .size(40.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.productName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "${NumberFormat.getNumberInstance(Locale.US).format(product.price)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE91E36)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(product.briefDescription, style = MaterialTheme.typography.bodyMedium, color = SubtleGray)
                Spacer(modifier = Modifier.height(12.dp))
                Text(product.fullDescription, style = MaterialTheme.typography.bodyMedium, color = SubtleGray)
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Quantity:", style = MaterialTheme.typography.titleMedium, color = Color.Black)
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { if (quantity > 1) quantity-- }, modifier = Modifier.size(40.dp)) {
                      Text("-",  color = Color.Black)
                    }
                    Text(quantity.toString(), modifier = Modifier.padding(horizontal = 16.dp), fontWeight = FontWeight.Bold, color = Color.Black)
                    IconButton(onClick = { quantity++ }, modifier = Modifier.size(40.dp)) {
                        Text("+",  color = Color.Black)
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onAddToCart,
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xFF6588E6)),
                        shape = RoundedCornerShape(16.dp)
                    ) { Text("Buy Now", color = Color.White, style = MaterialTheme.typography.titleMedium) }

                    Spacer(modifier = Modifier.width(16.dp))

                    OutlinedButton(
                        onClick = onAddToCart,
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFF6588E6)),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Add to Cart", tint = AccentBlue, modifier = Modifier.size(32.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
