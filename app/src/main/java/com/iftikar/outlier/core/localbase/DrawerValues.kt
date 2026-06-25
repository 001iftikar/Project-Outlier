package com.iftikar.outlier.core.localbase

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import com.iftikar.outlier.core.models.DrawerItem
import com.iftikar.outlier.feature.inbox.api.InboxNavKey
import com.iftikar.outlier.feature.post.api.PostNavKey

val drawerItems = listOf(
    DrawerItem(icon = Icons.Outlined.ChatBubbleOutline, title = "Inbox", navKey = InboxNavKey),
    DrawerItem(icon = Icons.Outlined.Add, title = "Post", navKey = PostNavKey)
)