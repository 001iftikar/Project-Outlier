package com.iftikar.outlier.feature.post.impl

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.iftikar.outlier.core.designsystem.component.bars.OutlierTopAppBar
import com.iftikar.outlier.core.designsystem.component.input.TextFieldComponent
import com.iftikar.outlier.core.designsystem.theme.LocalSpacing
import com.iftikar.outlier.feature.post.component.DetailsInputComponent
import com.iftikar.outlier.feature.post.component.FullScreenImagePreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(
    viewModel: PostViewModel
) {
    val spacing = LocalSpacing.current

    val state by viewModel.state.collectAsStateWithLifecycle()
    val action = viewModel::onAction

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 3),
        onResult = { newUris ->
            val combinedImages = (state.selectedImages + newUris).distinct().take(3)

            action(PostScreenAction.OnImageSelected(combinedImages))
        })

    Scaffold(
        topBar = {
            OutlierTopAppBar(
                title = { Text("Post") })
        }) { innerPadding ->
        if (state.showImageInFullScreen) {
            FullScreenImagePreview(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = spacing.horizontalPadding),
                isVisible = true,
                onDismiss = { action(PostScreenAction.OnFullScreenDismiss) },
                model = state.selectedImages[state.currentImageIndex],
                onRemoveClick = {action(PostScreenAction.OnPictureRemove(state.currentImageIndex))}
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(all = spacing.horizontalPadding),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                DetailsInputComponent(
                    text = "PROJECT TITLE"
                ) {
                    TextFieldComponent(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.title,
                        label = "e.g.: Arch based Linux Distro",
                        onValueChange = { action(PostScreenAction.OnTitleChange(it)) })
                }
            }

            item {
                DetailsInputComponent(
                    text = "PROJECT DESCRIPTION"
                ) {
                    TextFieldComponent(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.description,
                        label = "What makes your project special?",
                        onValueChange = { action(PostScreenAction.OnDescChange(it)) },
                        singleLine = false,
                        minLines = 3
                    )
                }
            }

            item {
                DetailsInputComponent(
                    text = "SHOW OFF"
                ) {
                    if (state.selectedImages.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }), contentAlignment = Alignment.Center
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Photo,
                                    contentDescription = "Select photos"
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "Upload Pictures",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier.clip(RoundedCornerShape(spacing.roundedCornerPadding))
                            ) {
                                AsyncImage(
                                    model = state.selectedImages[state.currentImageIndex],
                                    contentDescription = "Image ${state.currentImageIndex + 1}",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .aspectRatio(16f / 9f)
                                        .clickable(onClick = { action(PostScreenAction.OnShowFullScreen) })
                                )
                                Box(
                                    modifier = Modifier
                                        .background(
                                            MaterialTheme.colorScheme.surfaceVariant.copy(0.6f),
                                            CircleShape
                                        )
                                        .align(Alignment.TopEnd)
                                ) {
                                    Text(
                                        text = "${state.currentImageIndex + 1} / ${state.selectedImages.size}",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }
                            }
                            Spacer(Modifier.height(15.dp))
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (state.currentImageIndex > 0) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                        contentDescription = "Next picture preview",
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clickable(
                                                onClick = { action(PostScreenAction.OnPrevImage) }))
                                }
                                if (state.selectedImages.size < 3) {
                                    Icon(
                                        imageVector = Icons.Default.Upload,
                                        contentDescription = "Upload",
                                        modifier = Modifier.clickable(onClick = {
                                                photoPickerLauncher.launch(
                                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                )
                                            })
                                    )
                                    Spacer(Modifier.width(12.dp))
                                }
                                if (state.currentImageIndex < state.selectedImages.size - 1) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = "Next picture preview",
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clickable(
                                                onClick = { action(PostScreenAction.OnNextImage) }))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


























