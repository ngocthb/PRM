package com.example.project.navigation

import ShopProfileScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.project.ui.screens.*
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import com.example.project.ui.screens.ShopMapScreen
import com.example.project.ui.screens.ChatListScreen



@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Destinations.Splash
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
                onLoginSuccess = {
                    navController.navigate(Destinations.Home) {
                        popUpTo(Destinations.Login) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(Destinations.Register)
                }
            )
        }

        // RegisterScreen
        composable(Destinations.Register) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Destinations.Home) {
                        popUpTo(Destinations.Register) { inclusive = true }
                    }
                },
                onBackToLogin = { navController.popBackStack() }
            )
        }


        composable(Destinations.Home) {
            HomeScreen(
                onProductClick = { product ->
                    navController.navigate("${Destinations.ProductDetail}/${product.ProductID}")
                },
                navController = navController
            )
        }

        composable(
            route = "${Destinations.ProductDetail}/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: 0

            if (productId != 0) {
                // Pass navController here
                ProductDetail(
                    productId = productId,
                    navController = navController, // <-- Fixed
                    onAddToCart = { /* handle add to cart */ }
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

        composable(Destinations.ChatList) {
            ChatListScreen(navController = navController)
        }


        composable(
            route = "${Destinations.ChatDetail}/{chatId}",
            arguments = listOf(navArgument("chatId") { type = NavType.IntType })
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getInt("chatId") ?: 0
            if (chatId != 0) {

                ChatDetailScreen(
                    chatId = chatId,
                    navController = navController


                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No chat selected")
                }
            }
        }



        composable(Destinations.Cart) {
            CartScreen( navController = navController)
        }

        // Profile
        composable(Destinations.Profile) {
            ProfileScreen(navController = navController)
        }


        composable(
            route = "${Destinations.ShopProfile}/{shopId}",
            arguments = listOf(navArgument("shopId") { type = NavType.IntType })
        ) { backStackEntry ->
            val shopId = backStackEntry.arguments?.getInt("shopId") ?: 0
            if (shopId != 0) {

                ShopProfileScreen(
                    shopId = shopId,
                    navController = navController


                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No shop selected")
                }
            }
        }

        composable(Destinations.ShopMap) {
            ShopMapScreen()
        }


    }
}
