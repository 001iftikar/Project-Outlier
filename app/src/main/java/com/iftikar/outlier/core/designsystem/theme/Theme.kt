package com.iftikar.outlier.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Pink,
    secondary = PinkDark,
    tertiary = PinkLight,

    background = Black,
    surface = DarkGray,
    surfaceVariant = BorderGray,

    onPrimary = Color.White, // Text inside primary buttons should be white
    onSecondary = Color.White,
    onBackground = TextWhite, // Standard text on the black background
    onSurface = TextWhite, // Text on cards
    onSurfaceVariant = TextGray
)

@Composable
fun OutlierTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}