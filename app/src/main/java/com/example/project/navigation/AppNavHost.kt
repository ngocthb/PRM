package com.example.project.navigation

import ShopProfileScreen
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.*
import androidx.compose.ui.platform.LocalLayoutDirection
import com.example.project.ui.components.CustomSnackbarHost
import com.example.project.ui.screens.*
import com.example.project.ui.screens.ChatListScreen
import com.example.project.ui.screens.ShopMapScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project.ui.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    val loginViewModel: LoginViewModel = viewModel()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val layoutDirection = LocalLayoutDirection.current
    Scaffold(
        snackbarHost = { CustomSnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding  ->
        NavHost(
            navController = navController,
            startDestination = Destinations.Splash,
            modifier = Modifier
                .fillMaxSize()
                // chỉ áp padding ngang, bỏ top & bottom để tránh "hở"
                .padding(
                    start = innerPadding.calculateStartPadding(layoutDirection),
                    end = innerPadding.calculateEndPadding(layoutDirection)
                )
        ) {

            // SplashScreen
            composable(Destinations.Splash) {
                SplashScreen {
                    navController.navigate(Destinations.Login) {
                        popUpTo(Destinations.Splash) { inclusive = true }
                    }
                }
            }

            // LoginScreen
            composable(Destinations.Login) {
                LoginScreen(
                    viewModel = loginViewModel,
                    onLoginSuccess = {
                        navController.navigate(Destinations.Home) {
                            popUpTo(Destinations.Login) { inclusive = true }
                        }
                    },
                    onRegisterClick = {
                        navController.navigate(Destinations.Register)
                    },
                    snackbarHostState = snackbarHostState, // ✅ truyền xuống
                    scope = scope
                )
            }

            // RegisterScreen
            composable(Destinations.Register) {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate(Destinations.Login) {
                            popUpTo(Destinations.Register) { inclusive = true }
                        }
                    },
                    onBackToLogin = { navController.popBackStack() },
                    snackbarHostState = snackbarHostState, // ✅ truyền xuống
                    scope = scope
                )
            }

            // Home
            composable(Destinations.Home) {

                HomeScreen(
                    onProductClick = { product ->
                        navController.navigate("${Destinations.ProductDetail}/${product.productId}")
                    },
                    navController = navController,
                    viewModel = loginViewModel
                )
            }

            // Product Detail
            composable(
                route = "${Destinations.ProductDetail}/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.IntType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getInt("productId") ?: 0
                if (productId != 0) {
                    ProductDetail(
                        productId = productId,
                        navController = navController,
                        snackbarHostState = snackbarHostState,
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No product selected")
                    }
                }
            }

            // Chat List
            composable(Destinations.ChatList) {
                ChatListScreen(navController = navController)
            }

            // Chat Detail
            composable(
                route = "${Destinations.ChatDetail}/{chatId}",
                arguments = listOf(navArgument("chatId") { type = NavType.IntType })
            ) { backStackEntry ->
                val chatId = backStackEntry.arguments?.getInt("chatId") ?: 0
                if (chatId != 0) {
                    ChatDetailScreen(chatId = chatId, navController = navController)
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No chat selected")
                    }
                }
            }

            // Cart
            composable(Destinations.Cart) {
                CartScreen(navController = navController)
            }

            // Profile
            composable(Destinations.Profile) {
                ProfileScreen(navController = navController,
                    onLogout = {
                        navController.navigate(Destinations.Login) {
                            // xóa back stack để user không back về các màn đã auth
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            launchSingleTop = true
                        }
                    })
            }

            // Shop Profile
            composable(
                route = "${Destinations.ShopProfile}/{shopId}",
                arguments = listOf(navArgument("shopId") { type = NavType.IntType })
            ) { backStackEntry ->
                val shopId = backStackEntry.arguments?.getInt("shopId") ?: 0
                if (shopId != 0) {
                    ShopProfileScreen(shopId = shopId, navController = navController)
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No shop selected")
                    }
                }
            }

            // Shop Map
            composable(Destinations.ShopMap) {
                ShopMapScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}