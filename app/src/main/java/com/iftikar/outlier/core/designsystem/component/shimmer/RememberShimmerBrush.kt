package com.iftikar.outlier.core.designsystem.component.shimmer

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun rememberShimmerBrush(): Brush {

    val transition = rememberInfiniteTransition(
        label = "post-feed-shimmer"
    )

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    val screenWidthPx = with(LocalDensity.current) {
        screenWidth.toPx()
    }

    val shimmerOffset by transition.animateFloat(
        initialValue = -screenWidthPx,
        targetValue = screenWidthPx * 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1300,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer-offset"
    )

    val baseColor = MaterialTheme.colorScheme.surfaceVariant

    val highlightColor = MaterialTheme.colorScheme.onSurface.copy(
        alpha = 0.10f
    )

    return Brush.linearGradient(
        colors = listOf(
            baseColor,
            highlightColor,
            baseColor
        ),
        start = Offset(
            x = shimmerOffset - 250f,
            y = 0f
        ),
        end = Offset(
            x = shimmerOffset,
            y = 250f
        )
    )
}