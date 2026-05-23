package com.zenx.one.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.zenx.one.ui.components.OneButton
import com.zenx.one.ui.components.OneTopBar
import com.zenx.one.ui.theme.OnePurple
import com.zenx.one.ui.viewmodel.CartViewModel
import com.zenx.one.ui.viewmodel.DeliveryOption

@Composable
fun CartScreen(
    cartViewModel: CartViewModel,
    onBack: () -> Unit = {}
) {
    val uiState by cartViewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            OneTopBar(
                title = "My Cart",
                showBack = true,
                onBackClick = onBack,
                showCart = false // already in cart
            )

            if (uiState.items.isEmpty()) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text("Your cart is empty", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.items) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.product.name, fontWeight = FontWeight.Bold)
                                    Text("${(item.product.finalPrice ?: item.product.priceOriginal ?: 0.0).toInt()} L x ${item.quantity}")
                                }
                            }
                        }
                    }
                }

                // Total and Checkout buttons
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp,
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .navigationBarsPadding()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("${cartViewModel.totalPrice().toInt()} L", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = OnePurple)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OneButton(
                                text = "PRE PAY",
                                isPrimary = true,
                                onClick = { cartViewModel.onPrepayClick() },
                                modifier = Modifier.weight(1f)
                            )
                            OneButton(
                                text = "POST PAY",
                                isPrimary = false,
                                onClick = { cartViewModel.onPostpayClick() },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Card Details Popup
        if (uiState.showCardPopup) {
            CardDetailsDialog(
                onDismiss = { cartViewModel.dismissCardPopup() },
                onConfirm = { card, exp, cvv -> cartViewModel.onCardDetailsSubmitted(card, exp, cvv) }
            )
        }

        // Delivery Option Popup
        if (uiState.showDeliveryOption) {
            DeliveryOptionDialog(
                onOptionSelected = { cartViewModel.selectDeliveryOption(it) },
                onDismiss = { cartViewModel.dismissDeliveryOption() }
            )
        }

        // Success State
        if (uiState.paymentConfirmed) {
            PaymentSuccessOverlay(
                deliveryOption = uiState.deliveryOption,
                onDismiss = { cartViewModel.resetPayment() }
            )
        }
    }
}

@Composable
fun CardDetailsDialog(onDismiss: () -> Unit, onConfirm: (String, String, String) -> Unit) {
    var cardNumber by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Enter Card Details", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                OutlinedTextField(
                    value = cardNumber,
                    onValueChange = { cardNumber = it },
                    label = { Text("Card Number") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = expiry,
                        onValueChange = { expiry = it },
                        label = { Text("MM/YY") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = cvv,
                        onValueChange = { cvv = it },
                        label = { Text("CVV") },
                        modifier = Modifier.weight(1f)
                    )
                }
                OneButton(
                    text = "CONFIRM PAYMENT",
                    onClick = { onConfirm(cardNumber, expiry, cvv) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun DeliveryOptionDialog(onOptionSelected: (DeliveryOption) -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("How would you like to receive your order?", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                OneButton(
                    text = "PICK UP AT ONE STORE",
                    onClick = { onOptionSelected(DeliveryOption.STORE_PICKUP) },
                    modifier = Modifier.fillMaxWidth()
                )
                OneButton(
                    text = "COURIER DELIVERY",
                    isPrimary = false,
                    onClick = { onOptionSelected(DeliveryOption.COURIER) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun PaymentSuccessOverlay(deliveryOption: DeliveryOption?, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("✓", fontSize = 48.sp, color = Color(0xFF22C55E))
                Text("Payment Successful!", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = when(deliveryOption) {
                        DeliveryOption.STORE_PICKUP -> "Your order is ready for pickup at the nearest One store."
                        DeliveryOption.COURIER -> "A courier will deliver your order soon."
                        null -> "Thank you for your purchase!"
                    },
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                OneButton(text = "CLOSE", onClick = onDismiss, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
