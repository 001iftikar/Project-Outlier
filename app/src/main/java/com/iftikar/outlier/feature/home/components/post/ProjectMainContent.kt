package com.iftikar.outlier.feature.home.components.post

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun ProjectMainContent(
    projectImages: List<String>,
    projectTitle: String
) {
    val pagerState = rememberPagerState(
        pageCount = { projectImages.size }
    )

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth()
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
                        .padding(
                            horizontal = 6.dp,
                            vertical = 4.dp
                        ),
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