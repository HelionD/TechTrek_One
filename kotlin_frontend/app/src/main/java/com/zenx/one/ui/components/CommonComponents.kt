package com.zenx.one.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenx.one.R
import com.zenx.one.ui.theme.OnePurple
import com.zenx.one.ui.theme.OneYellow

@Composable
fun OneTopBar(
    title: String? = null,
    subtitle: String? = null,
    showBack: Boolean = false,
    onBackClick: () -> Unit = {},
    showCart: Boolean = false,
    cartViewModel: com.zenx.one.ui.viewmodel.CartViewModel? = null,
    onCartClick: () -> Unit = {},
    showFilter: Boolean = false,
    onFilterClick: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Logo and Title area
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (showBack) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_back),
                        contentDescription = "Back",
                        tint = OnePurple
                    )
                }
            }
            OneLogo()
            if (title != null) {
                Column {
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            color = Color.Gray,
                            fontSize = 11.sp
                        )
                    }
                    Text(
                        text = title,
                        color = OnePurple,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        letterSpacing = (-0.5).sp
                    )
                }
            } else {
                Text(
                    text = "",
                    color = OnePurple,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    letterSpacing = (-0.5).sp
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            actions()
            if (showCart && cartViewModel != null) {
                val cartState = cartViewModel.uiState.collectAsState().value
                val count = cartState.items.sumOf { it.quantity }
                BadgedBox(
                    badge = {
                        if (count > 0) {
                            Badge(containerColor = OnePurple) {
                                Text(count.toString(), color = Color.White)
                            }
                        }
                    },
                    modifier = Modifier.padding(end = 8.dp).clickable { onCartClick() }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_basket),
                        contentDescription = "Cart",
                        tint = OnePurple,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            if (showFilter) {
                IconButton(onClick = onFilterClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_filter),
                        contentDescription = "Filter",
                        tint = OnePurple
                    )
                }
            }
        }
    }
}

@Composable
fun OneLogo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.logo_png),
        contentDescription = "One Logo",
        modifier = modifier.height(40.dp),
        contentScale = ContentScale.Fit
    )
}

@Composable
fun OneBottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp
    ) {
        val items = listOf(
            Triple(R.drawable.ic_account_box, "Profile",  "profile"),
            Triple(R.drawable.ic_document,    "Home",     "home"),
            Triple(R.drawable.ic_basket,      "Shop",     "shop"),
            Triple(R.drawable.ic_basket,      "Cart",     "cart"),
            Triple(R.drawable.ic_settings,    "Settings", "settings"),
            Triple(R.drawable.ic_menu,        "Menu",     "menu"),
        )
        items.forEach { (icon, label, route) ->
            NavigationBarItem(
                selected = currentRoute == route,
                onClick = { onNavigate(route) },
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

@Composable
fun OneButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPrimary) OneYellow else OnePurple,
            contentColor = if (isPrimary) Color.White else Color.White
        )
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}
