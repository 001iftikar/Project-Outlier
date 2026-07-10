package com.iftikar.outlier.feature.post.impl

import android.net.Uri

data class PostScreenState (
    val title: String = "",
    val description: String = "",
    val selectedImages: List<Uri> = emptyList(),
    val currentImageIndex: Int = 0,
    val showImageInFullScreen: Boolean = false
)

sealed interface PostScreenAction {
    data class OnTitleChange(val title: String) : PostScreenAction
    data class OnDescChange(val desc: String) : PostScreenAction
    data class OnImageSelected(val images: List<Uri>) : PostScreenAction
    data object OnNextImage : PostScreenAction
    data object OnPrevImage : PostScreenAction
    data object OnShowFullScreen : PostScreenAction
    data object OnFullScreenDismiss : PostScreenAction
    data class OnPictureRemove(val index: Int) : PostScreenAction
}