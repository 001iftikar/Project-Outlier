package com.iftikar.outlier.feature.post.impl

import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iftikar.outlier.core.datastore.SessionManager
import com.iftikar.outlier.core.domain.repository.PostRepository
import com.iftikar.outlier.core.domain.repository.StorageRepository
import com.iftikar.outlier.core.models.SendPost
import com.iftikar.outlier.core.result.CreatePostError
import com.iftikar.outlier.core.result.onError
import com.iftikar.outlier.core.result.onSuccess
import com.iftikar.outlier.feature.post.api.PostNavKey
import com.iftikar.outlier.feature.post.impl.img_classify.ImageModerator
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = PostViewModel.Factory::class)
class PostViewModel @AssistedInject constructor(
    private val storageRepository: StorageRepository,
    private val postRepository: PostRepository,
    private val sessionManager: SessionManager,
    private val imageModerator: ImageModerator,
    @Assisted private val args: PostNavKey
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(args: PostNavKey): PostViewModel
    }

    val stateToSurviveArg = args.stateToSurvive
    private val _state = MutableStateFlow(
        PostScreenState(
            stateToSurvive = stateToSurviveArg ?: StateToSurvive()
        )
    )
    val state = _state.asStateFlow()

    private val _event: Channel<PostScreenEvent> = Channel()
    val event = _event.receiveAsFlow()

    fun onAction(action: PostScreenAction) {
        when (action) {
            is PostScreenAction.OnTitleChange -> {
                _state.update { currentState ->
                    currentState.copy(
                        stateToSurvive = currentState.stateToSurvive.copy(title = action.title)
                    )
                }
            }

            is PostScreenAction.OnDescChange -> _state.update {
                it.copy(
                    stateToSurvive = it.stateToSurvive.copy(
                        description = action.desc
                    )
                )
            }

            is PostScreenAction.OnImageSelected -> _state.update {
                it.copy(
                    stateToSurvive = it.stateToSurvive.copy(
                        selectedImages = action.images
                    )
                )
            }

            PostScreenAction.OnNextImage -> _state.update {
                it.copy(
                    stateToSurvive = it.stateToSurvive.copy(
                        currentImageIndex = it.stateToSurvive.currentImageIndex + 1
                    )
                )
            }

            PostScreenAction.OnPrevImage -> _state.update {
                it.copy(
                    stateToSurvive = it.stateToSurvive.copy(
                        currentImageIndex = it.stateToSurvive.currentImageIndex - 1
                    )
                )
            }

            PostScreenAction.OnFullScreenDismiss -> _state.update { it.copy(showImageInFullScreen = false) }
            PostScreenAction.OnShowFullScreen -> _state.update { it.copy(showImageInFullScreen = true) }
            is PostScreenAction.OnPictureRemove -> {
                val currentImages = _state.value.stateToSurvive.selectedImages
                if (action.index in currentImages.indices) {
                    val updatedList = currentImages.toMutableList()
                    updatedList.removeAt(action.index)

                    _state.update { currentState ->
                        val shouldShowFullScreen =
                            updatedList.isNotEmpty() && currentState.showImageInFullScreen
                        val nextIndex =
                            if (currentState.stateToSurvive.currentImageIndex >= updatedList.size && updatedList.isNotEmpty()) {
                                updatedList.size - 1
                            } else {
                                maxOf(0, currentState.stateToSurvive.currentImageIndex)
                            }

                        currentState.copy(
                            stateToSurvive = currentState.stateToSurvive.copy(
                                selectedImages = updatedList.toList(),
                                currentImageIndex = nextIndex
                            ),
                            showImageInFullScreen = shouldShowFullScreen
                        )
                    }
                }
            }

            is PostScreenAction.OnTechStackChange -> _state.update {
                it.copy(
                    stateToSurvive = it.stateToSurvive.copy(
                        techStack = action.techStack
                    )
                )
            }

            is PostScreenAction.OnGithubUrlChange -> _state.update {
                it.copy(
                    stateToSurvive = it.stateToSurvive.copy(
                        githubUrl = action.url
                    )
                )
            }

            is PostScreenAction.OnLiveProjectUrlChange -> _state.update {
                it.copy(
                    stateToSurvive = it.stateToSurvive.copy(
                        liveProjectUrl = action.url
                    )
                )
            }

            is PostScreenAction.OnTagsChange -> _state.update {
                it.copy(
                    stateToSurvive = it.stateToSurvive.copy(
                        tags = action.tags.lowercase()
                    )
                )
            }

            PostScreenAction.OnPublishClick -> checkImage(_state.value.stateToSurvive.selectedImages)
        }
    }

    private fun checkImage(imageStrings: List<String>) {
        val imageUris = imageStrings.map { it.toUri() }

        try {
            viewModelScope.launch {
                imageUris.forEach { uri ->
                    val result = imageModerator.classify(uri)
                    Log.d("CheckImage- ${imageUris.indexOf(uri)}", "checkImage: $result")
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }
    private fun post() {
        viewModelScope.launch {
            _state.update { it.copy(isPosting = true) }
            try {
                val userId = ""
                if (userId == null) {
                    _event.send(PostScreenEvent.OnError("Session is expired, please log in and try again"))
                    return@launch
                }
                val currentState = _state.value
                val ids =
                    storageRepository.uploadFiles(currentState.stateToSurvive.selectedImages.map { str ->
                        str.toUri()
                    })
                val sendPost = SendPost(
                    userId = userId,
                    title = currentState.stateToSurvive.title,
                    description = currentState.stateToSurvive.description,
                    images = ids,
                    techStack = currentState.stateToSurvive.techStack,
                    githubUrl = currentState.stateToSurvive.githubUrl,
                    liveUrl = currentState.stateToSurvive.liveProjectUrl,
                    tags = currentState.stateToSurvive.tags
                )
                postRepository.createPost(sendPost, userId = userId).onSuccess {
                    _state.update { it.copy(isPosting = false) }
                    _event.send(PostScreenEvent.OnSuccess("SendPost Published"))
                }.onError { ex ->
                    _state.update { it.copy(isPosting = false) }
                    when (ex) {
                        CreatePostError.UNAUTHORIZED -> _event.send(PostScreenEvent.OnError("Session is expired, please log in and try again"))
                        CreatePostError.CONFLICT -> _event.send(PostScreenEvent.OnError("Data already exists"))
                        CreatePostError.TOO_MANY_REQUESTS -> _event.send(PostScreenEvent.OnError("Too many requests, please try again later"))
                        CreatePostError.SERVER -> _event.send(PostScreenEvent.OnError("Oops! This is a server error"))
                        CreatePostError.TIMEOUT -> _event.send(PostScreenEvent.OnError("Timeout, try again later"))
                        CreatePostError.NO_INTERNET -> _event.send(PostScreenEvent.OnError("You need to check your internet connection and try again"))
                        CreatePostError.UNKNOWN -> _event.send(PostScreenEvent.OnError("Oops! Something went wrong"))
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                _event.send(PostScreenEvent.OnError("Image upload failed"))
            }
        }
    }
}