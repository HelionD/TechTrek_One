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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Top bar - matches Figma "One Shop" header
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
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = OnePurple,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("1", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                    }
                }
                Text(
                    text = "One Shop",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }

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
                        if (product.imageUrl != null) {
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
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "${(product.finalPrice ?: product.priceOriginal ?: 0.0).toInt()} L",
                                color = OnePurple,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp
                            )
                            if (product.finalPrice != null && product.priceOriginal != null) {
                                Text(
                                    text = "${product.priceOriginal.toInt()} L",
                                    color = Color(0xFF9E9E9E),
                                    fontSize = 16.sp,
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
                    }
                }
            }

            // Payment confirmed card (Frame 11)
            AnimatedVisibility(
                visible = uiState.paymentConfirmed,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "✓",
                            fontSize = 40.sp,
                            color = Color(0xFF22C55E)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Payment Confirmed",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                        uiState.paymentMethod?.let {
                            Text(
                                text = if (it == PaymentMethod.PRE_PAY) "Paid with Prepay" else "Added to Postpay Bill",
                                color = Color(0xFF9E9E9E),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(120.dp))
        }

        // Bottom action buttons — Frame 9: Wishlist + Cart | Frame 10+: PrePay + PostPay
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (!uiState.paymentConfirmed) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OneButton(
                        text = "ADD TO WISHLIST",
                        isPrimary = true,
                        onClick = {
                            // Add to wishlist (favorites)
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

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OneButton(
                        text = "PRE PAY",
                        isPrimary = true,
                        onClick = { cartViewModel.confirmPayment(PaymentMethod.PRE_PAY) },
                        modifier = Modifier.weight(1f)
                    )
                    OneButton(
                        text = "POST PAY",
                        isPrimary = false,
                        onClick = { cartViewModel.confirmPayment(PaymentMethod.POST_PAY) },
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OneButton(
                        text = "PRE PAY",
                        isPrimary = true,
                        onClick = { cartViewModel.confirmPayment(PaymentMethod.PRE_PAY) },
                        modifier = Modifier.weight(1f)
                    )
                    OneButton(
                        text = "POST PAY",
                        isPrimary = false,
                        onClick = { cartViewModel.confirmPayment(PaymentMethod.POST_PAY) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
