package com.example.project.ui.screens

import CategoryTabs
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.project.model.Product
import com.example.project.ui.screens.components.*
import com.example.project.ui.viewmodel.HomeViewModel
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color


@Composable
fun HomeScreen(
    categories: List<String> = listOf("All", "Popular", "Recent", "Recommended"),
    onCategorySelected: (String) -> Unit = {},
    onProductClick: (Product) -> Unit = {},
    navController: NavController,
    viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
) {
    val currency by viewModel.currency.collectAsState()

    // Gọi API 1 lần khi màn hình mở
    LaunchedEffect(Unit) {
        viewModel.getCurrencyRate()
    }

    var selectedCategory by remember { mutableStateOf(categories.first()) }
    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }
    val allProducts = remember {
        listOf(
            Product(1, "Casual V-neck", 29.99, "https://bizweb.dktcdn.net/thumb/1024x1024/100/399/392/products/6-2.png", "Clothes", "Simple v-neck tee", "Comfortable cotton casual t-shirt with V-neck style.", "Material: Cotton, Sizes: S/M/L/XL"),
            Product(2, "Casual T-shirt", 19.99, "https://bizweb.dktcdn.net/thumb/1024x1024/100/399/392/products/6-2.png", "Clothes", "Basic casual t-shirt", "Soft cotton t-shirt, perfect for everyday wear.", "Material: Cotton, Sizes: S/M/L/XL"),
            Product(3, "White Blouse", 39.99, "https://bizweb.dktcdn.net/thumb/1024x1024/100/399/392/products/6-2.png", "Clothes", "Elegant white blouse", "Elegant white blouse for office and casual events.", "Material: Polyester, Sizes: S/M/L"),
            Product(4, "Denim Jacket", 59.99, "https://bizweb.dktcdn.net/thumb/1024x1024/100/399/392/products/6-2.png", "Clothes", "Trendy denim jacket", "Stylish denim jacket, perfect for layering.", "Material: Denim, Sizes: S/M/L/XL"),
            Product(5, "Black Hoodie", 49.99, "https://bizweb.dktcdn.net/thumb/1024x1024/100/399/392/products/6-2.png", "Clothes", "Warm black hoodie", "Cozy black hoodie with front pocket and hood.", "Material: Cotton/Polyester, Sizes: S/M/L/XL"),
            Product(6, "Sports Shorts", 24.99, "https://bizweb.dktcdn.net/thumb/1024x1024/100/399/392/products/6-2.png", "Clothes", "Comfortable sports shorts", "Lightweight shorts for running or gym workouts.", "Material: Polyester, Sizes: S/M/L/XL"),
            Product(7, "Running Shoes", 79.99, "https://bizweb.dktcdn.net/thumb/1024x1024/100/399/392/products/6-2.png", "Shoes", "Lightweight running shoes", "Breathable shoes designed for long-distance running.", "Material: Mesh, Sizes: 38-45"),
            Product(8, "Leather Boots", 129.99, "https://bizweb.dktcdn.net/thumb/1024x1024/100/399/392/products/6-2.png", "Shoes", "Durable leather boots", "Classic leather boots for casual and formal wear.", "Material: Leather, Sizes: 39-45"),
            Product(9, "Casual Cap", 14.99, "https://bizweb.dktcdn.net/thumb/1024x1024/100/399/392/products/6-2.png", "Accessories", "Simple casual cap", "Adjustable cap perfect for outdoor activities.", "Material: Cotton, One size fits all"),
            Product(10, "Sunglasses", 39.99, "https://bizweb.dktcdn.net/thumb/1024x1024/100/399/392/products/6-2.png", "Accessories", "Stylish sunglasses", "UV protection sunglasses with modern design.", "Lens: UV400, Frame: Plastic")
        )
    }

    val filteredProducts = allProducts.filter {
        selectedCategory == "All" || it.CategoryID == selectedCategory
    }

    Scaffold(
        containerColor = Color(0xFFFAFAFA),
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Header
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    TopBar()
                    SearchBar()
                    Spacer(Modifier.height(16.dp))
                    PromoBanner()
                    currency?.let {
                        Text(text = "Tỷ giá USD/VND: ${it.quotes["USDVND"]}")
                    }
                    Spacer(Modifier.height(16.dp))
                    CategoryTabs(
                        selectedCategoryId = selectedCategoryId,
                        onCategoryClick = { id ->
                            selectedCategoryId = id // cập nhật state khi click
                        }
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }

            // Grid products
            items(filteredProducts) { product ->
                ProductCard(product = product, onProductClick = onProductClick)
            }
        }
    }
}
