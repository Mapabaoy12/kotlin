package com.example.pasteleriamilsabores.navigation

import com.example.pasteleriamilsabores.data.repo.ProductRepository
import com.example.pasteleriamilsabores.viewmodel.ProductViewModel


import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pasteleriamilsabores.data.local.AppDatabase
import com.example.pasteleriamilsabores.data.repo.CartRepository
import com.example.pasteleriamilsabores.ui.screen.CartScreen
import com.example.pasteleriamilsabores.ui.screen.HomeScreen
import com.example.pasteleriamilsabores.ui.screen.LoginScreen
import com.example.pasteleriamilsabores.ui.screen.ProductDetailScreen
import com.example.pasteleriamilsabores.ui.screen.RegisterScreen
import com.example.pasteleriamilsabores.viewmodel.CartViewModel
import com.example.pasteleriamilsabores.viewmodel.LoginViewModel
import com.example.pasteleriamilsabores.viewmodel.RegisterViewModel

sealed class Screen(val route: String) {
    object Register : Screen("register")
    object Login : Screen("login")
    object Home: Screen("home")
    object Detail: Screen("detail/{productId}") {
        fun createRoute(id: Int) = "detail/$id"
    }
    object Cart: Screen("cart")
}

@Composable
fun NavGraph(
    appContext: Context,
    navController: NavHostController,
    paddingValues: PaddingValues
) {
    val db = AppDatabase.getInstance(appContext)
    val productRepository = ProductRepository(appContext)
    val cartRepository = CartRepository(db)

    val productViewModel: ProductViewModel = viewModel(factory = ProductViewModel.factory(productRepository))
    val cartViewModel: CartViewModel = viewModel(factory = CartViewModel.factory(cartRepository))
    val registerViewModel: RegisterViewModel = viewModel(factory = RegisterViewModel.factory())
    val loginViewModel: LoginViewModel = viewModel(factory = LoginViewModel.factory())

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                paddingValues = PaddingValues(16.dp),
                viewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                paddingValues = PaddingValues(16.dp),
                viewModel = registerViewModel,
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onBackToLogin = { navController.popBackStack() }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = productViewModel,
                cartViewModel = cartViewModel,
                onProductClick = { id -> navController.navigate(Screen.Detail.createRoute(id)) },
                onCartClick = { navController.navigate(Screen.Cart.route) }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("productId") ?: 0
            ProductDetailScreen(
                productId = id,
                viewModel = productViewModel,
                cartViewModel = cartViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Cart.route) {
            CartScreen(
                viewModel = cartViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}