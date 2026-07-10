package com.iftikar.outlier.feature.post.component

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FullScreenImagePreview(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onRemoveClick: () -> Unit,
    model: Any
) {
    // 1. Handle the physical/system back swipe
    BackHandler(enabled = isVisible) {
        onDismiss()
    }

    // 2. The outer visibility controls the background fade
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(300)),
        exit = fadeOut(tween(300)),
        modifier = modifier.zIndex(100f) // Ensures it floats above all other UI
    ) {
        // The Scrim (Darkened Background)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
                // Detect taps on the background to dismiss
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { onDismiss() })
                },
            contentAlignment = Alignment.Center
        ) {
            // 3. The Content Container
            Box(
                modifier = Modifier
                    // Apply a specific pop/scale animation to the content
                    // independently of the background fade
                    .animateEnterExit(
                        enter = scaleIn(
                            animationSpec = tween(300, easing = LinearOutSlowInEasing),
                            initialScale = 0.8f
                        ) + fadeIn(tween(300)),
                        exit = scaleOut(
                            animationSpec = tween(200, easing = FastOutLinearInEasing),
                            targetScale = 0.8f
                        ) + fadeOut(tween(200))
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { /* Consume tap */ })
                    }
            ) {
                AsyncImage(
                    model = model,
                    contentDescription = null
                )
                IconButton(
                    onClick = onRemoveClick,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove"
                    )
                }
            }
        }
    }
}