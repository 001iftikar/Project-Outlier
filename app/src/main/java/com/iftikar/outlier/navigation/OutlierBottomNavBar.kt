package com.iftikar.outlier.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iftikar.outlier.feature.home.api.HomeNavKey
import com.iftikar.outlier.feature.inbox.api.InboxNavKey
import com.iftikar.outlier.feature.profile.api.ProfileNavKey
import com.iftikar.outlier.feature.search.api.SearchNavKey
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.HazeColorEffect
import dev.chrisbanes.haze.blur.hazeBlur

@Composable
fun OutlierBottomNavBar(
    visible: Boolean,
    selectedTab: MainTabNavKey,
    hazeState: HazeState,
    onTabSelected: (MainTabNavKey) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(32.dp)

    val tintColor = MaterialTheme.colorScheme.primary.copy(
        alpha = 0.15f
    )

    val blurStyle = remember(tintColor) {
        HazeBlurStyle {
            blurRadius(16.dp)

            colorEffects(
                listOf(
                    HazeColorEffect.tint(tintColor)
                )
            )
        }
    }

    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = slideInVertically(
            animationSpec = tween(400),
            initialOffsetY = { it * 2 }
        ),
        exit = slideOutVertically(
            animationSpec = tween(400),
            targetOffsetY = { it * 2 }
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(
                    horizontal = 16.dp,
                    vertical = 12.dp
                )
        ) {

            // Blur is now restricted to the pill's bounds.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .hazeBlur(
                        input = HazeInput.Sources(hazeState),
                        style = blurStyle
                    )
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = shape,
                    color = MaterialTheme.colorScheme.surface.copy(
                        alpha = 0.10f
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(
                            alpha = 0.35f
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 8.dp,
                                vertical = 8.dp
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        BottomNavItem(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Outlined.Home,
                            label = "home",
                            selected = selectedTab == HomeNavKey,
                            onClick = {
                                onTabSelected(HomeNavKey)
                            }
                        )

                        BottomNavItem(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Outlined.Search,
                            label = "search",
                            selected = selectedTab == SearchNavKey,
                            onClick = {
                                onTabSelected(SearchNavKey)
                            }
                        )

                        BottomNavItem(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Outlined.ChatBubbleOutline,
                            label = "inbox",
                            selected = selectedTab == InboxNavKey,
                            onClick = {
                                onTabSelected(InboxNavKey)
                            }
                        )

                        BottomNavItem(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Outlined.Person,
                            label = "profile",
                            selected = selectedTab == ProfileNavKey,
                            onClick = {
                                onTabSelected(ProfileNavKey)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val tint = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onBackground
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {

        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )

        Text(
            text = label,
            color = tint,
            fontSize = 10.sp,
            fontWeight = if (selected) {
                FontWeight.SemiBold
            } else {
                FontWeight.Normal
            }
        )
    }
}