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


val AccentBlue = Color(0xFF6588E6)
val SubtleGray = Color(0xFF888888)
val LightSurface = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetail(
    productId: Int,
    navController: NavController,
    onAddToCart: () -> Unit = {}
) {
    LaunchedEffect(productId) {
        Log.d("ProductDetailScreen", "Received productId: $productId")
    }

    val product = remember(productId) {
        Product(
            ProductID = productId,
            ProductName = "Pink Blazer",
            Price = 250.0,
            ImageURL = "https://th.bing.com/th?id=OIF.%2bBYOth1qtrGy4epcdX%2bWIw&rs=1&pid=ImgDetMain&o=7&rm=3",
            CategoryID = "Clothes",
            BriefDescription = "Pink blazer with soft material, not hot comfortable laying, available in various sizes. Suitable for use at parties.",
            FullDescription = "Comfortable cotton casual t-shirt with V-neck style.",
            TechnicalSpecifications = "Material: Cotton, Sizes: S/M/L/XL"
        )
    }

    var quantity by remember { mutableStateOf(1) }
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Scaffold(
        // We'll remove the TopAppBar from here to place the back button manually
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding( bottom = paddingValues.calculateBottomPadding()) // Apply only bottom padding from scaffold if needed
            // horizontal and top padding will be handled differently
        ) {
            // Box for Image and Back Button Overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight * 1 / 2) // Image height 2/3 of device height
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = product.ImageURL,
                        // placeholder = painterResource(id = R.drawable.placeholder_image),
                        // error = painterResource(id = R.drawable.error_image)
                    ),
                    contentDescription = product.ProductName,
                    modifier = Modifier
                        .fillMaxSize() // Image fills the Box
                        .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)), // Optional: Clip bottom corners
                    contentScale = ContentScale.Crop
                )

                // Back Button
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 16.dp, top = 32.dp) // tăng top để nút xuống thấp hơn
                        .background(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                            CircleShape
                        )
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

            }

            // Content below the image
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp) // Padding for the content area
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Name and Price
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.ProductName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$${product.Price}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE91E36)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Description
                Text(
                    text = product.BriefDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SubtleGray
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Quantity Selector
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Quantity:",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = { if (quantity > 1) quantity-- },
                        modifier = Modifier.size(40.dp)
                    ) { Icon(Icons.Default.Delete, "Decrease quantity") }

                    Text(
                        text = quantity.toString(),
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = { quantity++ },
                        modifier = Modifier.size(40.dp)
                    ) { Icon(Icons.Default.Add, "Increase quantity") }
                }

                Spacer(modifier = Modifier.weight(1f)) // Push buttons to bottom

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onAddToCart,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xFF6588E6)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Buy Now",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    OutlinedButton(
                        onClick = onAddToCart,
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp,Color(0xFF6588E6)),
                        contentPadding = PaddingValues(0.dp)

                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Add to Cart",
                            tint = AccentBlue,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp)) // Add some bottom padding
            }
        }
    }
}
