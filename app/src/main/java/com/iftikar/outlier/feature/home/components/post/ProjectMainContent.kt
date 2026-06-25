package com.iftikar.outlier.feature.home.components.post

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage

@Composable
fun ProjectMainContent(
    projectImage: String,
    projectTitle: String
) {
    AsyncImage(
        model = projectImage,
        contentDescription = null,
        contentScale = ContentScale.Fit
    )

    Text(
        text = projectTitle,
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.bodyMedium,
        maxLines = 2
    )
}