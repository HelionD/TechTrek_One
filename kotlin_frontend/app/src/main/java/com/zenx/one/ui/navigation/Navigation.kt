package com.zenx.one.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zenx.one.R
import com.zenx.one.ui.screens.*
import com.zenx.one.ui.viewmodel.CartViewModel
import com.zenx.one.ui.viewmodel.ShopViewModel

sealed class Screen(val route: String) {
    object Home     : Screen("home")
    object Shop     : Screen("shop")
    object Product  : Screen("product/{productId}") {
        fun createRoute(id: String) = "product/$id"
    }
    object Cart     : Screen("cart")
    object Wishlist : Screen("wishlist")
    object Profile  : Screen("profile")
    object Settings : Screen("settings")
    object Menu     : Screen("menu")
}

data class BottomNavItem(
    val screen: Screen,
    val iconRes: Int,
    val label: String
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Profile,  R.drawable.ic_account_box, "Profile"),
    BottomNavItem(Screen.Home,     R.drawable.ic_document,    "Home"),
    BottomNavItem(Screen.Shop,     R.drawable.ic_basket,      "Shop"),
    BottomNavItem(Screen.Cart,     R.drawable.ic_basket,      "Cart"),
    BottomNavItem(Screen.Settings, R.drawable.ic_settings,    "Settings"),
    BottomNavItem(Screen.Menu,     R.drawable.ic_menu,        "Menu"),
)

@Composable
fun OneNavHost(
    navController: NavHostController = rememberNavController(),
    cartViewModel: CartViewModel = viewModel(),
    shopViewModel: ShopViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    NavHost(navController = navController, startDestination = Screen.Home.route, modifier = modifier) {

        composable(Screen.Home.route) {
            HomeScreen(
                onShopClick = { navController.navigate(Screen.Shop.route) }
            )
        }

        composable(Screen.Shop.route) {
            ShopScreen(
                shopViewModel = shopViewModel,
                cartViewModel = cartViewModel,
                onProductClick = { product ->
                    cartViewModel.selectProduct(product)
                    navController.navigate(Screen.Product.createRoute(product.id))
                },
                onCartClick = { navController.navigate(Screen.Cart.route) }
            )
        }

        composable(Screen.Product.route) {
            ProductDetailScreen(
                cartViewModel = cartViewModel,
                onBack = { navController.popBackStack() },
                onPaymentConfirmed = {
                    // stay on the screen, it shows confirmed state inline
                }
            )
        }

        composable(Screen.Cart.route) {
            CartScreen(
                cartViewModel = cartViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Wishlist.route) {
            WishlistScreen(
                cartViewModel = cartViewModel,
                onProductClick = { product ->
                    cartViewModel.selectProduct(product)
                    navController.navigate(Screen.Product.createRoute(product.id))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }

        composable(Screen.Settings.route) {
            SettingsScreen()
        }

        composable(Screen.Menu.route) {
            SettingsScreen()
        }
    }
}
