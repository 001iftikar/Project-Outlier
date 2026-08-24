package com.iftikar.outlier.feature.home.components.drawer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.iftikar.outlier.core.designsystem.component.spinner.LoadingSpinner
import com.iftikar.outlier.core.designsystem.theme.LocalSpacing
import com.iftikar.outlier.core.models.DrawerItem
import com.iftikar.outlier.feature.inbox.api.InboxNavKey
import com.iftikar.outlier.feature.post.api.PostNavKey
import com.iftikar.outlier.feature.shared.DrawerUserInfoState

@Composable
fun DrawerContent(
    drawerUserInfoState: DrawerUserInfoState,
    drawerState: DrawerState,
    onDrawerItemClick: (NavKey) -> Unit,
    onRefetchClick: () -> Unit
) {
    val spacing = LocalSpacing.current
    DismissibleDrawerSheet(
        drawerState = drawerState, drawerContainerColor = MaterialTheme.colorScheme.surfaceDim
    ) {
        if (drawerUserInfoState.isDrawerLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingSpinner(Modifier.size(36.dp))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(horizontal = spacing.horizontalPadding),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
                ) {
                    if (drawerUserInfoState.drawerUserInfo == null) {
                        TextButton(
                            onClick = onRefetchClick
                        ) {
                            Text(
                                text = "Hi, failed to get your info, click here",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    } else {
                        Text(
                            text = "Hi, ${drawerUserInfoState.drawerUserInfo.name}",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
                DrawerItemComponent(
                    DrawerItem(
                        icon = Icons.Outlined.ChatBubbleOutline,
                        title = "Inbox",
                        navKey = InboxNavKey
                    ),
                    onClick = { onDrawerItemClick(it) },
                )

                if (drawerUserInfoState.drawerUserInfo != null) {
                    if (drawerUserInfoState.drawerUserInfo.isDeveloper) {
                        DrawerItemComponent(
                            DrawerItem(icon = Icons.Outlined.Add, title = "Create Post", navKey = PostNavKey),
                            onClick = { onDrawerItemClick(it) },
                        )
                    }
                }
            }
        }
    }
}