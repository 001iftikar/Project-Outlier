package com.iftikar.outlier.feature.home.components.post

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import kotlin.math.abs


@Composable
fun ProjectMainContent(
    projectImages: List<String>,
    projectTitle: String,
    drawerState: DrawerState
) {
    val pagerState = rememberPagerState(
        pageCount = { projectImages.size }
    )

    val scope = rememberCoroutineScope()
    val touchSlop = LocalViewConfiguration.current.touchSlop

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(pagerState, drawerState, touchSlop) {

                    awaitEachGesture {

                        val down = awaitFirstDown(
                            pass = PointerEventPass.Initial,
                            requireUnconsumed = false
                        )

                        // A right swipe on the first image should open the drawer.
                        val shouldOpenDrawer =
                            pagerState.currentPage == 0 &&
                                    drawerState.currentValue == DrawerValue.Closed &&
                                    drawerState.targetValue == DrawerValue.Closed

                        var totalX = 0f
                        var totalY = 0f
                        var drawerTriggered = false

                        do {
                            val event = awaitPointerEvent(
                                pass = PointerEventPass.Initial
                            )

                            val change = event.changes.firstOrNull {
                                it.id == down.id
                            } ?: break

                            totalX += change.position.x -
                                    change.previousPosition.x

                            totalY += change.position.y -
                                    change.previousPosition.y

                            // Detect a rightward horizontal swipe.
                            if (
                                shouldOpenDrawer &&
                                !drawerTriggered &&
                                totalX > touchSlop &&
                                totalX > abs(totalY)
                            ) {
                                drawerTriggered = true

                                // Prevent HorizontalPager from consuming this drag.
                                change.consume()

                                // Open the existing navigation drawer.
                                scope.launch {
                                    drawerState.open()
                                }
                            } else if (drawerTriggered) {
                                // Consume the remaining drag events.
                                change.consume()
                            }

                        } while (
                            event.changes.any {
                                it.id == down.id && it.pressed
                            }
                        )
                    }
                }
        ) {

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),

                // Disable image swiping whenever the drawer isn't fully closed.
                userScrollEnabled =
                    drawerState.currentValue == DrawerValue.Closed &&
                            drawerState.targetValue == DrawerValue.Closed

            ) { pageIndex ->

                val imageUrl = projectImages[pageIndex]

                var imageAspectRatio by remember(imageUrl) {
                    mutableFloatStateOf(16f / 9f)
                }

                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Project image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(imageAspectRatio),
                    onSuccess = { state ->

                        val imageSize = state.painter.intrinsicSize

                        val width = imageSize.width
                        val height = imageSize.height

                        if (
                            width.isFinite() &&
                            height.isFinite() &&
                            width > 0f &&
                            height > 0f
                        ) {
                            imageAspectRatio = if (width > height) {
                                16f / 9f
                            } else {
                                9f / 16f
                            }
                        }
                    }
                )
            }

            // Image pager indicators.
            if (projectImages.size > 1) {

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(
                            Color(0xFF18181B).copy(alpha = 0.4f)
                        )
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(5.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    projectImages.forEachIndexed { index, _ ->

                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(
                                    if (pagerState.currentPage == index) {
                                        Color.Blue
                                    } else {
                                        Color.Gray
                                    }
                                )
                        )
                    }
                }
            }
        }

        Text(
            text = projectTitle,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
