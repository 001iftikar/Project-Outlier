package com.iftikar.outlier.feature.post.impl

import android.net.Uri

data class PostScreenState (
    val title: String = "",
    val description: String = "",
    val selectedImages: List<Uri> = emptyList(),
    val currentImageIndex: Int = 0,
    val showImageInFullScreen: Boolean = false,
    val techStack: String = "",
    val githubUrl: String = "",
    val liveProjectUrl: String = "",
    val tags: String = "",
    val isPosting: Boolean = false
) {
    val buttonText: String
        get() = if (!isPosting) "Publish" else "Posting"
    val buttonEnabled: Boolean
        get() = title.isNotEmpty() && description.isNotEmpty() && selectedImages.isNotEmpty() && techStack.isNotEmpty() && (githubUrl.isNotEmpty() || liveProjectUrl.isNotEmpty()) && !isPosting
}

sealed interface PostScreenAction {
    data class OnTitleChange(val title: String) : PostScreenAction
    data class OnDescChange(val desc: String) : PostScreenAction
    data class OnImageSelected(val images: List<Uri>) : PostScreenAction
    data object OnNextImage : PostScreenAction
    data object OnPrevImage : PostScreenAction
    data object OnShowFullScreen : PostScreenAction
    data object OnFullScreenDismiss : PostScreenAction
    data class OnPictureRemove(val index: Int) : PostScreenAction
    data class OnTechStackChange(val techStack: String) : PostScreenAction
    data class OnGithubUrlChange(val url: String) : PostScreenAction
    data class OnLiveProjectUrlChange(val url: String) : PostScreenAction
    data class OnTagsChange(val tags: String) : PostScreenAction
    data object OnPublishClick : PostScreenAction
}

sealed interface PostScreenEvent {
    data class OnError(val error: String) : PostScreenEvent
    data class OnSuccess(val message: String) : PostScreenEvent
}

















