package com.iftikar.outlier.feature.home.impl

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import com.iftikar.outlier.core.designsystem.theme.LocalSpacing
import com.iftikar.outlier.feature.home.components.DrawerItems
import com.iftikar.outlier.feature.home.components.HomeTopBar
import kotlinx.coroutines.launch

@Composable
fun HomeScreen() {
    val spacing = LocalSpacing.current

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrimAlpha by animateFloatAsState(
        targetValue = if (drawerState.targetValue == DrawerValue.Open) 0.3f else 0.0f,
        label = "Scrim Alpha"
    )

    DismissibleNavigationDrawer(
        modifier = Modifier
            .fillMaxSize(),
        drawerState = drawerState,
        drawerContent = {
            DrawerItems(drawerState = drawerState)
        },
        gesturesEnabled = true
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    drawContent() // Render the Scaffold, TopBar, and List normally

                    if (scrimAlpha > 0f) {
                        // Paint a black rectangle over the entire Scaffold canvas
                        drawRect(color = Color.Black, alpha = scrimAlpha)
                    }
                }
                .clickable(
                    enabled = drawerState.targetValue == DrawerValue.Open,
                    onClick = { scope.launch { drawerState.close() } },
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ),
            topBar = {
                HomeTopBar(
                    drawerState = drawerState,
                    onMenuClick = {
                        if (drawerState.isClosed) {
                            scope.launch { drawerState.open() }
                        } else {
                            scope.launch { drawerState.close() }
                        }
                    }
                )
            }) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = spacing.horizontalPadding)
            ) {
                for (i in 1..100) {
                    Text(
                        text = "let me in >> $i"
                    )
                }
            }
        }
    }
}