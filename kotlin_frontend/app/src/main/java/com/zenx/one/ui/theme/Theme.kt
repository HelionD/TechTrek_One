package com.zenx.one.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val OneColorScheme = lightColorScheme(
    primary = OnePurple,
    onPrimary = OneSurface,
    primaryContainer = OnePurpleLight,
    onPrimaryContainer = OneSurface,
    secondary = OneYellow,
    onSecondary = OneOnSurface,
    secondaryContainer = OneYellowLight,
    onSecondaryContainer = OneOnSurface,
    background = OneBackground,
    onBackground = OneOnSurface,
    surface = OneSurface,
    onSurface = OneOnSurface,
    surfaceVariant = OneSurfaceVariant,
    onSurfaceVariant = OneOnSurfaceVariant,
    outline = OneOutline,
    error = OneError,
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = OnePurple.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = OneColorScheme,
        typography = Typography,
        content = content
    )
}
