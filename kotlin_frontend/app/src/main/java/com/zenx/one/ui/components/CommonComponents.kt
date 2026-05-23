package com.zenx.one.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    showFilter: Boolean = false,
    onFilterClick: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(OnePurple)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Logo area
        Row(verticalAlignment = Alignment.CenterVertically) {
            OneLogo()
            if (title != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    }
                    Text(
                        text = title,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            actions()
            if (showFilter) {
                IconButton(onClick = onFilterClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_filter),
                        contentDescription = "Filter",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun OneLogo(modifier: Modifier = Modifier) {
    // "1" circle + "one" text — matching the Figma logo
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Surface(
            shape = MaterialTheme.shapes.small,
            color = OneYellow,
            modifier = Modifier.size(28.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "1",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
            }
        }
        Text(
            text = "one",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }
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
