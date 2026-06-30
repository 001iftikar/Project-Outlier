package com.iftikar.outlier.feature.home.impl

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.descope.Descope
import com.iftikar.outlier.core.designsystem.theme.LocalSpacing
import com.iftikar.outlier.feature.home.components.drawer.DrawerContent
import com.iftikar.outlier.feature.home.components.bars.HomeTopBar
import com.iftikar.outlier.feature.home.components.post.PostComponent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onDrawerItemClick: (NavKey) -> Unit
) {
    val spacing = LocalSpacing.current

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrimAlpha by animateFloatAsState(
        targetValue = if (drawerState.targetValue == DrawerValue.Open) 0.3f else 0.0f,
        label = "Scrim Alpha"
    )
    val topbarScrollBehaviour = TopAppBarDefaults.enterAlwaysScrollBehavior()

    DismissibleNavigationDrawer(
        modifier = Modifier
            .fillMaxSize(),
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(drawerState = drawerState, onDrawerItemClick = { navKey ->
                onDrawerItemClick(navKey)
                scope.launch { drawerState.close() }
            })
        },
        gesturesEnabled = true
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(topbarScrollBehaviour.nestedScrollConnection)
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
                    },
                    scrollBehavior = topbarScrollBehaviour
                )
            }) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = spacing.horizontalPadding),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                items(5) {
                    PostComponent()
                }
            }
        }
    }
}