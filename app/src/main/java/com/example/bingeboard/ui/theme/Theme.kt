package com.example.bingeboard.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = GoldAccent,
    secondary = SecondaryText,
    tertiary = ChipBorder,
    background = Background,
    surface = CardSurface,
    onPrimary = Background,
    onSecondary = PrimaryText,
    onTertiary = PrimaryText,
    onBackground = PrimaryText,
    onSurface = PrimaryText,
)

@Composable
fun BingeBoardTheme(
    darkTheme: Boolean = true, // Force dark theme as per design
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
