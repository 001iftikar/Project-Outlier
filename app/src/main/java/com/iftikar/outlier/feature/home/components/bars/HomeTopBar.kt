package com.iftikar.outlier.feature.home.components.bars

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import com.iftikar.outlier.core.designsystem.component.bars.OutlierTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    drawerState: DrawerState,
    onMenuClick: () -> Unit,
    onHeartClick: () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior
) {
    OutlierTopAppBar(
        title = {},
        actions = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onMenuClick
                ) {
                    Crossfade(
                        targetState = drawerState.targetValue,
                        animationSpec = tween(durationMillis = 200),
                        label = "Menu Icon Fade"
                    ) { targetDrawerState ->
                        if (targetDrawerState == DrawerValue.Closed) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Open Menu"
                            )
                        }
                    }
                }

                Text(
                    text = "Outlier",
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = FontFamily.Cursive
                )

                IconButton(
                    onClick = onHeartClick
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = "Notifications"
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior
    )
}






















