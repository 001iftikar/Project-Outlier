package com.iftikar.outlier.feature.home.components.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.iftikar.outlier.core.designsystem.theme.LocalSpacing

@Composable
fun ProfileComponent(
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit
) {
    val spacing = LocalSpacing.current
    Box(
        modifier = modifier
            .padding(
                start = spacing.horizontalPadding,
                bottom = 24.dp
            )
            .size(58.dp)
            .shadow(
                elevation = 8.dp,
                shape = CircleShape
            )
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.tertiary
                    )
                )
            )
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.surface,
                shape = CircleShape
            )
            .clickable(
                onClick = onProfileClick
            ),
        contentAlignment = Alignment.Center
    ) {

        Icon(
            imageVector = Icons.Outlined.Person,
            contentDescription = "Open profile",
            tint = Color.White,
            modifier = Modifier.size(30.dp)
        )
    }
}