package com.iftikar.outlier.feature.home.components.post

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ModeComment
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.iftikar.outlier.core.models.Post

@Composable
fun PostComponent(
    post: Post
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
            projectTitle = post.title
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProjectReactComponent(
                icon = Icons.Outlined.FavoriteBorder,
                text = "${post.likesCount}",
                contentDescription = "likes"
            )
            ProjectReactComponent(
                icon = Icons.Outlined.ModeComment,
                text = "${post.commentsCount}",
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





















