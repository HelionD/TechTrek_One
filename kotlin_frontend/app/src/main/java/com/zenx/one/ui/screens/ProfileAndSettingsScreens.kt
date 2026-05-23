package com.zenx.one.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenx.one.ui.components.OneLogo
import com.zenx.one.ui.theme.OnePurple
import com.zenx.one.ui.theme.OneYellow

import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.zenx.one.ui.navigation.Screen

// ─── Profile Screen ────────────────────────────────────────────────────────

@Composable
fun ProfileScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // ... (Header code same)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(OnePurple)
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            Column {
                OneLogo()
                Spacer(modifier = Modifier.height(24.dp))
                // Avatar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = OneYellow,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "A",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 28.sp
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "ANNA",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "+355 69 123 45 67",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 13.sp
                        )
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = "Postpaid",
                                color = Color.White,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProfileMenuItem(icon = "❤️", title = "My Wishlist", subtitle = "View saved products", onClick = {
                navController.navigate(Screen.Wishlist.route)
            })
            ProfileMenuItem(icon = "📋", title = "My Plan", subtitle = "Post 100")
            ProfileMenuItem(icon = "📱", title = "My Device", subtitle = "Not set")
            ProfileMenuItem(icon = "🎁", title = "My Discounts", subtitle = "View personalized offers")
            ProfileMenuItem(icon = "📊", title = "Data Usage", subtitle = "0.0 GB used")
            ProfileMenuItem(icon = "🌐", title = "Language", subtitle = "Albanian (sq)")
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun ProfileMenuItem(icon: String, title: String, subtitle: String, onClick: () -> Unit = {}) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(text = icon, fontSize = 22.sp)
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(text = subtitle, color = Color(0xFF9E9E9E), fontSize = 12.sp)
            }
            Text(text = "›", color = Color(0xFFBDBDBD), fontSize = 20.sp)
        }
    }
}

// ─── Settings Screen ───────────────────────────────────────────────────────

@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(OnePurple)
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            OneLogo()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Settings",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            listOf(
                "🔔" to "Notifications",
                "🔒" to "Privacy",
                "🌐" to "Language",
                "📞" to "Contact Support",
                "ℹ️" to "About",
                "🚪" to "Log Out"
            ).forEach { (icon, label) ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(text = icon, fontSize = 20.sp)
                        Text(
                            text = label,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Text(text = "›", color = Color(0xFFBDBDBD), fontSize = 20.sp)
                    }
                }
            }
        }
    }
}
