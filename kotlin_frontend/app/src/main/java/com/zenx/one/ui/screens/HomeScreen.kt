package com.zenx.one.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenx.one.R
import com.zenx.one.ui.components.OneLogo
import com.zenx.one.ui.components.OneTopBar
import com.zenx.one.ui.theme.OnePurple
import com.zenx.one.ui.theme.OnePurpleDark
import com.zenx.one.ui.theme.OneYellow

@Composable
fun HomeScreen(
    onShopClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Top bar - account info header
        HomeTopBar()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Balance + Top Up cards
            AccountCard()

            // Discover banner
            DiscoverBanner(onShopClick = onShopClick)

            // Help and support
            HelpSection()

            Spacer(modifier = Modifier.height(80.dp)) // bottom nav space
        }
    }
}

@Composable
private fun HomeTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(OnePurple)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OneLogo()
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "Service of ANNA",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 11.sp
            )
            Text(
                text = "One me Parapagese  +355 69 123 45 67",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun AccountCard() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Balance card
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_home),
                        contentDescription = null,
                        tint = Color(0xFF9E9E9E),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Balance",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
                Text(
                    text = "Valid until:",
                    color = Color(0xFF9E9E9E),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = "0 ALL",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    text = "N/A",
                    color = Color(0xFF9E9E9E),
                    fontSize = 11.sp
                )
            }
        }

        // Top Up options
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TopUpCard(
                icon = R.drawable.ic_favorite,
                title = "Top up",
                subtitle = "self topup"
            )
            TopUpCard(
                icon = R.drawable.ic_account_box,
                title = "Top up",
                subtitle = "for someone else"
            )
        }
    }
}

@Composable
private fun TopUpCard(icon: Int, title: String, subtitle: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF5F5F5),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = null,
                        tint = OnePurple,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Text(
                    text = subtitle,
                    color = Color(0xFF9E9E9E),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun DiscoverBanner(onShopClick: () -> Unit) {
    Column {
        Text(
            text = "Discover",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            shape = RoundedCornerShape(16.dp),
            onClick = onShopClick
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(OnePurple, OnePurpleDark)
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Roaming",
                        color = OneYellow,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp
                    )
                    Text(
                        text = "included in",
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "your plan.",
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
                // 5G badge
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                    shape = RoundedCornerShape(4.dp),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "5G",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Text(
                    text = "Enjoy 🌍 Europe",
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                )
            }
        }

        // Dot indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(5) { i ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(if (i == 0) 8.dp else 6.dp)
                        .background(
                            if (i == 0) OnePurple else Color(0xFFBDBDBD),
                            RoundedCornerShape(50)
                        )
                )
            }
        }
    }
}

@Composable
private fun HelpSection() {
    Column {
        Text(
            text = "Help and support",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf(
                "Login to the Application",
                "Main User Panel (Dashboard)",
                "Service Manage..."
            ).forEach { title ->
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        color = Color(0xFF424242),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}
