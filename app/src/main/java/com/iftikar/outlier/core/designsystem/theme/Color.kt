package com.iftikar.outlier.core.designsystem.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Pink = Color(0xFFFD366E)
val PinkDark = Color(0xFFD82558)
val PinkLight = Color(0xFFFF6B96)

// Deep Backgrounds
val Black = Color(0xFF000000) // True black for the deepest background
val DarkGray = Color(0xFF18181B) // Slightly elevated surface for cards/menus
val BorderGray = Color(0xFF27272A) // For subtle outlines

// Text Colors
val TextWhite = Color(0xFFF4F4F5) // Bright white for headings
val TextGray = Color(0xFFA1A1AA) // Muted gray for secondary text

val premiumVerticalGradient = Brush.verticalGradient(
    colors = listOf(
        Black, // Deep black at the top
        Pink.copy(alpha = 0.1f), // Soft, subtle pink glow in the dead center
        Black // Fades back to deep black at the bottom
    )
)