package com.zenx.one.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.zenx.one.R
import com.zenx.one.data.model.PaymentMethod
import com.zenx.one.ui.components.OneButton
import com.zenx.one.ui.components.OneLogo
import com.zenx.one.ui.theme.OnePurple
import com.zenx.one.ui.theme.OneYellow
import com.zenx.one.ui.viewmodel.CartViewModel

@Composable
fun ProductDetailScreen(
    cartViewModel: CartViewModel,
    onBack: () -> Unit = {},
    onPaymentConfirmed: () -> Unit = {}
) {
    val uiState by cartViewModel.uiState.collectAsState()
    val product = uiState.selectedProduct ?: return
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            cartViewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = onBack, modifier = Modifier.size(32.dp)) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = "Back",
                            tint = OnePurple,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    OneLogo()
                    Text(
                        text = "",
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp,
                        letterSpacing = (-0.5).sp,
                        color = OnePurple
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                // Product image card - Frame 9/10 style
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column {
                        // Image area
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .background(Color(0xFFF8F8F8)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!product.imageUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(product.imageUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = product.name,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(24.dp)
                                )
                            } else {
                                Text(
                                    text = product.name.take(1),
                                    fontSize = 64.sp,
                                    color = Color(0xFFBDBDBD)
                                )
                            }
                        }

                        // Product name + price
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = product.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "${(product.finalPrice ?: product.priceOriginal ?: 0.0).toInt()} L",
                                    color = OnePurple,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 26.sp
                                )
                                if (product.priceOriginal != null && (product.finalPrice ?: 0.0) < product.priceOriginal) {
                                    Text(
                                        text = "${product.priceOriginal.toInt()} L",
                                        color = Color.LightGray,
                                        fontSize = 18.sp,
                                        textDecoration = TextDecoration.LineThrough
                                    )
                                }
                            }

                            // Discount reasoning (LLM explanation)
                            if (!product.reasoning.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFF3E8FF)
                                ) {
                                    Text(
                                        text = "✨ ${product.reasoning}",
                                        color = OnePurple,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }

                            // Specs
                            if (!product.description.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = product.description,
                                    color = Color(0xFF666666),
                                    fontSize = 13.sp,
                                    lineHeight = 20.sp
                                )
                            }

                            // Specs grid (from JSONB)
                            if (!product.specs.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "DETAILS",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp,
                                    color = Color(0xFF9E9E9E),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                product.specs.forEach { (key, value) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(key, color = Color(0xFF6B7280), fontSize = 13.sp, modifier = Modifier.weight(1f))
                                        Text(value, fontWeight = FontWeight.Medium, fontSize = 13.sp, modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(120.dp))
            }

            // Bottom action buttons — Frame 9: Wishlist + Cart
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OneButton(
                        text = "ADD TO WISHLIST",
                        isPrimary = true,
                        onClick = {
                            cartViewModel.addToWishlist(product)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    OneButton(
                        text = "ADD TO CART",
                        isPrimary = false,
                        onClick = {
                            cartViewModel.addToCart(product)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
