package com.zenx.one.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zenx.one.R
import com.zenx.one.data.model.Product
import com.zenx.one.ui.components.*
import com.zenx.one.ui.theme.OnePurple
import com.zenx.one.ui.theme.OneYellow
import com.zenx.one.ui.viewmodel.CartViewModel
import com.zenx.one.ui.viewmodel.ShopViewModel

@Composable
fun ShopScreen(
    shopViewModel: ShopViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel(),
    onProductClick: (Product) -> Unit = {},
    onCartClick: () -> Unit = {}
) {
    val uiState by shopViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            shopViewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            OneTopBar(
                showCart = true,
                cartViewModel = cartViewModel,
                onCartClick = onCartClick,
                showFilter = true,
                onFilterClick = { /* TODO */ }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {
            // Category tabs
            CategoryTabs(
                selected = uiState.selectedCategory,
                onSelect = { shopViewModel.selectCategory(it) }
            )

            // Product grid
            if (uiState.isLoading) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(6) {
                        ProductCardSkeleton()
                    }
                }
            } else if (uiState.error != null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Failed to load products", color = Color(0xFF9E9E9E))
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = { shopViewModel.loadProducts() },
                            colors = ButtonDefaults.buttonColors(containerColor = OnePurple)
                        ) {
                            Text("Retry")
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(
                        start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.products) { product ->
                        ProductCard(
                            product = product,
                            onClick = { onProductClick(product) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryTabs(
    selected: String?,
    onSelect: (String?) -> Unit
) {
    val categories = listOf(null to "All", "telefona" to "Phones", "wearables" to "Wearables")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { (cat, label) ->
            val isSelected = cat == selected
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) OnePurple else Color(0xFFF0F0F0),
                onClick = { onSelect(cat)  }
            ) {
                Text(
                    text = label,
                    color = if (isSelected) Color.White else Color(0xFF424242),
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}
