package com.iftikar.outlier.feature.post.impl

import kotlinx.serialization.Serializable


data class PostScreenState(
    val stateToSurvive: StateToSurvive = StateToSurvive(),

    val showImageInFullScreen: Boolean = false,
    val isPosting: Boolean = false
) {
    val buttonText: String
        get() = if (!isPosting) "Publish" else "Posting"
    val buttonEnabled: Boolean
        get() = stateToSurvive.title.isNotEmpty() && stateToSurvive.description.isNotEmpty() && stateToSurvive.selectedImages.isNotEmpty() && stateToSurvive.techStack.isNotEmpty() && (stateToSurvive.githubUrl.isNotEmpty() || stateToSurvive.liveProjectUrl.isNotEmpty()) && !isPosting
}

@Serializable
data class StateToSurvive(
    val title: String = "",
    val description: String = "",
    val selectedImages: List<String> = emptyList(),
    val currentImageIndex: Int = 0,
    val techStack: String = "",
    val githubUrl: String = "",
    val liveProjectUrl: String = "",
    val tags: String = ""
)

sealed interface PostScreenAction {
    data class OnTitleChange(val title: String) : PostScreenAction
    data class OnDescChange(val desc: String) : PostScreenAction
    data class OnImageSelected(val images: List<String>) : PostScreenAction
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

















