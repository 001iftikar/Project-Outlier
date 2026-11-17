package com.iftikar.outlier.feature.home.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iftikar.outlier.core.datastore.SessionManager
import com.iftikar.outlier.core.domain.repository.PostRepository
import com.iftikar.outlier.core.result.GetPostError
import com.iftikar.outlier.core.result.onError
import com.iftikar.outlier.core.result.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _state = MutableStateFlow(HomeScreenState())
    val state = _state.asStateFlow()

    private val _event = Channel<HomeScreenEvent>()
    val event = _event.receiveAsFlow()

    init {
        getPosts()
    }

    fun onAction(action: HomeScreenAction) {
        when (action) {
            HomeScreenAction.OnRetry -> {
                getPosts()
            }

            HomeScreenAction.OnLogout -> logout()
            HomeScreenAction.OnRefresh -> getPosts("", true)
            HomeScreenAction.OnPaging -> getPosts(
                cursor = _state.value.cursor ?: "",
                isPaging = true
            )
        }
    }

    fun onPostReact(react: PostReactionAction) {
        when (react) {
            is PostReactionAction.OnCommentClick -> TODO()
            is PostReactionAction.OnLikeClick -> likePost(react.postId)
            is PostReactionAction.OnUnlikeClick -> unlikePost(react.postId)
        }
    }

    private fun getPosts(
        cursor: String = "",
        shouldRefresh: Boolean = false,
        isPaging: Boolean = false
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = !isPaging,
                    error = null,
                    isRefreshing = shouldRefresh,
                    isPaging = isPaging
                )
            }
            postRepository.getPosts(5, cursor = cursor).onSuccess { (posts, cursor) ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        isPaging = false,
                        posts = if (shouldRefresh) {
                            posts
                        } else {
                            it.posts + posts
                        },
                        cursor = cursor
                    )
                }
            }.onError { ex ->
                when (ex) {
                    GetPostError.NO_INTERNET -> setError("You need to check the internet connection")
                    GetPostError.SERVER -> setError("Oops! This is a server error")
                    GetPostError.UNKNOWN -> setError("Oops! Something went wrong")
                    GetPostError.NO_DATA -> setError("No data found")
                    GetPostError.DEVELOPER_MESSED_UP -> setError("The developer messed up! Contact them.")
                }
            }
        }
    }

    private fun likePost(postId: String) {
        viewModelScope.launch {
            val unModifiedPosts = _state.value.posts
            _state.update { currentState ->
                currentState.copy(
                    posts = unModifiedPosts.map {
                        if (it.id == postId) {
                            it.copy(
                                likedByCurrentUser = true,
                                likesCount = it.likesCount + 1
                            )
                        } else {
                            it
                        }
                    }
                )
            }
            postRepository.likePost(postId).onSuccess {
                //  let's do nothing
            }.onError { _ ->
                _state.update { currentState ->
                    currentState.copy(
                        posts = unModifiedPosts
                    )
                }
                _event.send(HomeScreenEvent.ShowSnackBarError("Failed to like post!"))
            }
        }
    }

    private fun unlikePost(postId: String) {
        viewModelScope.launch {
            val unModifiedPosts = _state.value.posts
            _state.update { currentState ->
                currentState.copy(
                    posts = unModifiedPosts
                        .map {
                            if (it.id == postId) {
                                it.copy(
                                    likedByCurrentUser = false,
                                    likesCount = it.likesCount - 1
                                )
                            } else {
                                it
                            }
                        }
                )
            }
            postRepository.unlikePost(postId).onSuccess {
                //  let's do nothing
            }.onError { _ ->
                _state.update { currentState ->
                    currentState.copy(
                        posts = unModifiedPosts
                    )
                }
                _event.send(HomeScreenEvent.ShowSnackBarError("Failed to unlike post!"))
            }
        }
    }


    private fun setError(error: String) {
        _state.update { it.copy(isLoading = false, error = error, isRefreshing = false) }
    }

    private fun logout() {
        viewModelScope.launch {
            try {
                sessionManager.clearSession()
                _event.send(HomeScreenEvent.OnSuccessLogout)

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}







































