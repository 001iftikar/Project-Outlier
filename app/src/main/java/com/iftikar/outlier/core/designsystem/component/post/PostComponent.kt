package com.iftikar.outlier.core.designsystem.component.post

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ModeComment
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.iftikar.outlier.core.models.Post
import com.iftikar.outlier.feature.home.components.post.AuthorComponent
import com.iftikar.outlier.feature.home.components.post.ProjectReactComponent
import com.iftikar.outlier.feature.home.impl.PostReactionAction

@Composable
fun PostComponent(
    post: Post,
    showOnSearchResult: Boolean = false,
    onPostReactClick: (PostReactionAction) -> Unit = {},
    onViewPostClick: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                MaterialTheme.colorScheme.surface
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(
                    alpha = 0.35f
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // 1. Project image
        ProjectMainContent(
            projectImages = post.images
        )

        // 2. Developer name and post time
        AuthorComponent(
            name = post.postAuthor.name,
            profilePic = "https://images5.alphacoders.com/651/thumb-1920-651720.jpg",
            createdAt = post.createdAt
        )

        // 3. Project title
        Text(
            text = post.title,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        // 4. Project description
        Text(
            text = post.desc,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge
        )

        // 5. Tech stack chips
        TechStackChips(
            techStack = post.techStack,
            modifier = Modifier.fillMaxWidth()
        )

        // 6. Engagement actions

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (!showOnSearchResult) {
                ProjectReactComponent(
                    icon = if (post.likedByCurrentUser) {
                        Icons.Default.Favorite
                    } else {
                        Icons.Outlined.FavoriteBorder
                    },
                    tint = if (post.likedByCurrentUser) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        LocalContentColor.current
                    },
                    text = "${post.likesCount}",
                    contentDescription = "Likes",
                    onClick = {
                        onPostReactClick(
                            if (post.likedByCurrentUser) {
                                PostReactionAction.OnUnlikeClick(post.id)
                            } else {
                                PostReactionAction.OnLikeClick(post.id)
                            }
                        )
                    }
                )

                Spacer(Modifier.width(22.dp))

                ProjectReactComponent(
                    icon = Icons.Outlined.ModeComment,
                    text = "${post.commentsCount}",
                    contentDescription = "Comments",
                    onClick = {
                        onPostReactClick(
                            PostReactionAction.OnCommentClick(post.id)
                        )
                    }
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "${post.likesCount} likes",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(3.dp))
                    Text(
                        text = "${post.likesCount}",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // View post button
            val buttonShape = RoundedCornerShape(50)

            Row(
                modifier = Modifier
                    .clip(buttonShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.20f),
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                            )
                        ),
                        shape = buttonShape
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.55f),
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.20f)
                            )
                        ),
                        shape = buttonShape
                    )
                    .clickable {
                        onViewPostClick(post.id)
                    }
                    .padding(
                        horizontal = 18.dp,
                        vertical = 11.dp
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "View Post",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}