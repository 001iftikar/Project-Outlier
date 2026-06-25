package com.iftikar.outlier.feature.home.components.post

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ModeComment
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.iftikar.outlier.core.mockdatabase.projectReaction

@Composable
fun PostComponent() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        PosterComponent(
            name = "Carl Grimes",
            profilePic = "https://images5.alphacoders.com/651/thumb-1920-651720.jpg",
            techStack = "Android, Jetpack Compose, Java, C++"
        )

        ProjectMainContent(
            projectImage = "https://i.redd.it/6sgqlsimxpf21.png",
            projectTitle = "Outlier - Show your skills here if you do not have a fancy degree"
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            projectReaction.forEach { reaction ->
                ProjectReactComponent(
                    icon = reaction.icon,
                    text = reaction.text
                )
            }
        }

        Text(
            text = "#room #appwrite #hilt",
            color = MaterialTheme.colorScheme.tertiary,
            style = MaterialTheme.typography.bodySmall,
            overflow = TextOverflow.Ellipsis
        )
    }
}





















