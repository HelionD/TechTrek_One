package com.zenx.one

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.zenx.one.ui.navigation.OneNavHost
import com.zenx.one.ui.navigation.Screen
import com.zenx.one.ui.theme.MyApplicationTheme
import com.zenx.one.ui.theme.OnePurple
import com.zenx.one.ui.viewmodel.CartViewModel
import com.zenx.one.ui.viewmodel.ShopViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                OneApp()
            }
        }
    }
}

@Composable
fun OneApp() {
    val navController = rememberNavController()
    val cartViewModel: CartViewModel = viewModel()
    val shopViewModel: ShopViewModel = viewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Bottom nav items matching Figma — 5 icons
    val bottomItems = listOf(
        Triple(Screen.Profile,  R.drawable.ic_account_box, "Profile"),
        Triple(Screen.Home,     R.drawable.ic_document,    "Home"),
        Triple(Screen.Shop,     R.drawable.ic_basket,      "Shop"),
        Triple(Screen.Settings, R.drawable.ic_settings,    "Settings"),
        Triple(Screen.Menu,     R.drawable.ic_menu,        "Menu"),
    )

    // Hide bottom nav on product detail
    val showBottomNav = currentDestination?.route != Screen.Product.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF5F5F5),
        bottomBar = {
            if (showBottomNav) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 0.dp,
                ) {
                    bottomItems.forEach { (screen, icon, label) ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == screen.route
                        } == true

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(icon),
                                    contentDescription = label,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF1C1B1F),
                                unselectedIconColor = Color(0xFF8E8E93),
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        OneNavHost(
            navController = navController,
            cartViewModel = cartViewModel,
            shopViewModel = shopViewModel,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
