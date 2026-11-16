package com.iftikar.outlier.feature.home.components.post

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ModeComment
import androidx.compose.material3.DrawerState
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.iftikar.outlier.core.models.Post
import com.iftikar.outlier.feature.home.impl.PostReactionAction

@Composable
fun PostComponent(
    post: Post,
    drawerState: DrawerState,
    onPostReactClick: (PostReactionAction) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        AuthorComponent(
            name = post.postAuthor.name,
            profilePic = "https://images5.alphacoders.com/651/thumb-1920-651720.jpg",
            techStack = post.postAuthor.techStack.joinToString()
        )

        ProjectMainContent(
            projectImages = post.images,
            projectTitle = post.title,
            drawerState = drawerState
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProjectReactComponent(
                icon = if (!post.likedByCurrentUser) Icons.Outlined.FavoriteBorder else Icons.Default.Favorite,
                tint = if (!post.likedByCurrentUser) LocalContentColor.current else MaterialTheme.colorScheme.primary,
                text = "${post.likesCount}",
                onClick = if (!post.likedByCurrentUser) {
                    { onPostReactClick(PostReactionAction.OnLikeClick(post.id)) }
                } else {
                    {
                        onPostReactClick(
                            PostReactionAction.OnUnlikeClick(post.id)
                        )
                    }
                },
                contentDescription = "likes"
            )
            ProjectReactComponent(
                icon = Icons.Outlined.ModeComment,
                text = "${post.commentsCount}",
                onClick = { onPostReactClick(PostReactionAction.OnCommentClick(post.id)) },
                contentDescription = "comments"
            )
        }

        Text(
            text = post.tags.joinToString(),
            color = MaterialTheme.colorScheme.tertiary,
            style = MaterialTheme.typography.bodySmall,
            overflow = TextOverflow.Ellipsis
        )
    }
}





















