package com.iftikar.outlier.feature.post.impl

import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iftikar.outlier.core.datastore.SessionManager
import com.iftikar.outlier.core.domain.repository.PostRepository
import com.iftikar.outlier.core.models.SendPost
import com.iftikar.outlier.core.result.CreatePostError
import com.iftikar.outlier.core.result.onError
import com.iftikar.outlier.core.result.onSuccess
import com.iftikar.outlier.feature.post.api.PostNavKey
import com.iftikar.outlier.feature.post.impl.img_classify.ImageModerator
import com.iftikar.outlier.feature.post.impl.img_classify.SafetyLabel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.seconds

@HiltViewModel(assistedFactory = PostViewModel.Factory::class)
class PostViewModel @AssistedInject constructor(
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

    var job: Job? = null

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

            is PostScreenAction.OnGithubUrlChange -> {
                job?.cancel()
                val protocolRemovedUrl = action.url.removePrefix("https://")
                val githubIsHost = protocolRemovedUrl.startsWith("github.com", ignoreCase = true)
                if (githubIsHost) {
                    job = viewModelScope.launch {
                        delay(2.seconds)
                        checkGitHubUrl(action.url)
                    }
                }
                _state.update {

                    it.copy(
                        stateToSurvive = it.stateToSurvive.copy(
                            githubUrl = action.url
                        ),
                        isValidGitHubUrl = githubIsHost
                    )
                }
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

            PostScreenAction.OnPublishClick -> {
                job?.cancel()
                post()
            }
        }
    }

    /**
     * Returns true when at least one selected image
     * is classified as NSFW or NSFL.
     */
    private suspend fun checkImage(
        imageStrings: List<String>
    ): Boolean {

        for ((index, imageString) in imageStrings.withIndex()) {

            val result = imageModerator.classify(imageString.toUri())

            Log.d(
                "CheckImage-$index",
                "Classification result: $result"
            )

            if (
                result.label == SafetyLabel.NSFW ||
                result.label == SafetyLabel.NSFL
            ) {
                return true
            }
        }

        return false
    }

    private suspend fun checkGitHubUrl(url: String) {
        Log.d("Github", "Checking")
        postRepository.gitHubUrlCheck(url).onSuccess { valid ->
            Log.d("Github", "$valid")
            _state.update {
                it.copy(showFetchFromReadme = valid)
            }
        }.onError {
            // Fail silently
            _state.update {
                it.copy(showFetchFromReadme = false)
            }
        }
    }

    private fun post() {
        viewModelScope.launch {

            _state.update {
                it.copy(isPosting = true)
            }

            try {
                if (sessionManager.getRefreshToken() == null) return@launch _event.send(
                    PostScreenEvent.OnError("Authentication failed! Please login again.")
                )
                val currentState = _state.value
                val selectedImages = currentState.stateToSurvive.selectedImages

                // 1. Validate image count.
                if (selectedImages.isEmpty() || selectedImages.size > 3) {

                    _event.send(
                        PostScreenEvent.OnError(
                            "Select between 1 and 3 images."
                        )
                    )

                    return@launch
                }

                // 2. Perform local image moderation.
                if (checkImage(selectedImages)) {

                    _event.send(
                        PostScreenEvent.OnError(
                            "Uh-uh! Your images look SUS"
                        )
                    )

                    return@launch
                }

                // 3. Prepare post metadata and original image URIs.
                val sendPost = SendPost(
                    title = currentState.stateToSurvive.title,
                    description = currentState.stateToSurvive.description,
                    images = selectedImages,
                    techStack = currentState.stateToSurvive.techStack,
                    githubUrl = currentState.stateToSurvive.githubUrl,
                    liveUrl = currentState.stateToSurvive.liveProjectUrl,
                    tags = currentState.stateToSurvive.tags
                )

                // 4. Delegate compression, multipart upload and error mapping.
                postRepository.createPost(sendPost)
                    .onSuccess {

                        _event.send(
                            PostScreenEvent.OnSuccess(
                                "Post published successfully."
                            )
                        )
                        _state.update {
                            it.copy(
                                stateToSurvive = StateToSurvive(),
                                isPosting = false
                            )
                        }
                    }
                    .onError { error ->

                        val message = when (error) {

                            CreatePostError.UNAUTHORIZED ->
                                "Session expired. Please log in again."

                            CreatePostError.FORBIDDEN ->
                                "Only developer accounts can publish projects."

                            CreatePostError.INVALID_REQUEST ->
                                "Please check your post details and images."

                            CreatePostError.INVALID_GITHUB_URL ->
                                "The GitHub repository is unavailable or invalid."

                            CreatePostError.UNSAFE_URL ->
                                "One of your project URLs was identified as potentially harmful."

                            CreatePostError.PAYLOAD_TOO_LARGE ->
                                "Your images exceed the allowed upload size."

                            CreatePostError.IMAGE_UPLOAD_FAILED ->
                                "Image upload failed. Please try again."

                            CreatePostError.SERVICE_UNAVAILABLE ->
                                "URL safety verification is temporarily unavailable."

                            CreatePostError.CONFLICT ->
                                "Data already exists."

                            CreatePostError.TOO_MANY_REQUESTS ->
                                "Too many requests. Please try again later."

                            CreatePostError.SERVER ->
                                "A server error occurred. Please try again."

                            CreatePostError.TIMEOUT ->
                                "The request timed out. Please try again."

                            CreatePostError.NO_INTERNET ->
                                "Check your internet connection and try again."

                            CreatePostError.UNKNOWN ->
                                "Something went wrong while publishing your post."
                        }

                        _event.send(
                            PostScreenEvent.OnError(message)
                        )
                    }

            } catch (cancellation: CancellationException) {

                throw cancellation

            } catch (exception: Exception) {

                Log.e(
                    "PostViewModel",
                    "Post creation failed.",
                    exception
                )

                _event.send(
                    PostScreenEvent.OnError(
                        "Image moderation or post processing failed. Please try again."
                    )
                )

            } finally {

                _state.update {
                    it.copy(isPosting = false)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}