package com.iftikar.outlier.feature.home.components.post

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.iftikar.outlier.feature.shared.components.shimmer.ShimmerBlock
import com.iftikar.outlier.feature.shared.components.shimmer.rememberShimmerBrush

@Composable
fun PostFeedShimmer(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    itemCount: Int = 3
) {
    val brush = rememberShimmerBrush()

    LazyColumn(
        modifier = modifier.testTag("home_posts_shimmer"),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        userScrollEnabled = false,
        contentPadding = contentPadding
    ) {
        items(itemCount, key = { "shimmer-$it" }) {
            PostShimmerItem(
                brush = brush,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun PostShimmerItem(
    brush: Brush,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        // Author section
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            ShimmerBlock(
                modifier = Modifier.size(45.dp),
                brush = brush,
                shape = CircleShape
            )

            Spacer(Modifier.width(10.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {

                // Author name
                ShimmerBlock(
                    modifier = Modifier
                        .width(125.dp)
                        .height(12.dp),
                    brush = brush
                )

                // Author technology stack
                ShimmerBlock(
                    modifier = Modifier
                        .width(205.dp)
                        .height(10.dp),
                    brush = brush
                )
            }
        }

        // Project image
        ShimmerBlock(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f),
            brush = brush,
            shape = RoundedCornerShape(12.dp)
        )

        // Project title
        Column(
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {

            ShimmerBlock(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(13.dp),
                brush = brush
            )

            ShimmerBlock(
                modifier = Modifier
                    .fillMaxWidth(0.45f)
                    .height(13.dp),
                brush = brush
            )
        }

        // Like and comment section
        Row(
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            repeat(2) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    ShimmerBlock(
                        modifier = Modifier.size(18.dp),
                        brush = brush,
                        shape = CircleShape
                    )

                    ShimmerBlock(
                        modifier = Modifier
                            .width(24.dp)
                            .height(10.dp),
                        brush = brush
                    )
                }
            }
        }

        // Tags
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            ShimmerBlock(
                modifier = Modifier
                    .width(65.dp)
                    .height(18.dp),
                brush = brush,
                shape = RoundedCornerShape(5.dp)
            )

            ShimmerBlock(
                modifier = Modifier
                    .width(85.dp)
                    .height(18.dp),
                brush = brush,
                shape = RoundedCornerShape(5.dp)
            )

            ShimmerBlock(
                modifier = Modifier
                    .width(55.dp)
                    .height(18.dp),
                brush = brush,
                shape = RoundedCornerShape(5.dp)
            )
        }
    }
}


