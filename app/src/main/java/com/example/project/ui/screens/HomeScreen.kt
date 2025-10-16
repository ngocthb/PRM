package com.example.project.ui.screens

import CategoryTabs
import android.util.Log
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
import androidx.compose.ui.graphics.Color
import com.example.project.model.ProductResponse
import com.example.project.ui.viewmodel.LoginViewModel
import com.example.project.ui.viewmodel.ProductViewModel
import com.example.project.ui.viewmodel.CategoryViewModel

@Composable
fun HomeScreen(
    onProductClick: (ProductResponse) -> Unit,
    navController: NavController,
    viewModel: LoginViewModel,
    productViewModel: ProductViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {

    val uiState by viewModel.uiState.collectAsState()
    val productState by productViewModel.uiState.collectAsState()
    val categoryViewModel: CategoryViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val categoryState by categoryViewModel.uiState.collectAsState()

    Log.d("HomeScreen", "uiState: $productState")

    LaunchedEffect(Unit) {
        productViewModel.loadProducts()
        categoryViewModel.loadCategories()
    }

    val filtered = productViewModel.getFilteredProducts()

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
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    TopBar(
                        username = uiState.user?.username,
                        address = uiState.user?.address
                    )

                    SearchBar(
                        onSearchChange = { query ->
                            productViewModel.onSearchQueryChange(query)
                        }
                    )
                    Spacer(Modifier.height(16.dp))

                    CategoryTabs(
                        categories = categoryState.categories,
                        selectedCategoryId = productState.selectedCategoryId,
                        onCategoryClick = { id -> productViewModel.onCategorySelected(id) }
                    )

                    Spacer(Modifier.height(16.dp))
                }
            }

            // Hiển thị sản phẩm đã lọc
            items(filtered) { product ->
                ProductCard(
                    product = product,
                    onProductClick = { onProductClick(product) }
                )
            }

        }
    }
}
