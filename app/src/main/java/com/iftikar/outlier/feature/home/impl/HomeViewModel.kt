package com.iftikar.outlier.feature.home.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iftikar.outlier.core.datastore.SessionManager
import com.iftikar.outlier.core.domain.repository.PostRepository
import com.iftikar.outlier.core.result.GenericError
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

    private val _commentState = MutableStateFlow(PostCommentState())
    val commentState = _commentState.asStateFlow()
    private val _doCommentEvent = Channel<PostCommentEvent>()
    val doCommentEvent = _doCommentEvent.receiveAsFlow()

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

            HomeScreenAction.OnCommentDismiss -> {
                // Cleanups are important btw
                _state.update { it.copy(commentToOpen = null) }
                _commentState.value = PostCommentState()
            }
        }
    }

    fun onPostReact(react: PostReactionAction) {
        when (react) {
            is PostReactionAction.OnCommentClick -> {
                _state.update { it.copy(commentToOpen = react.postId) }
                getComments(postId = react.postId)
            }
            is PostReactionAction.OnLikeClick -> likePost(react.postId)
            is PostReactionAction.OnUnlikeClick -> unlikePost(react.postId)
        }
    }

    fun onPostCommentAction(action: PostCommentAction) {
        when(action) {
            is PostCommentAction.OnCommentChange -> _commentState.update { it.copy(comment = action.comment) }
            is PostCommentAction.DoComment -> doComment(action.postId, action.author)
            is PostCommentAction.OnLoadMore -> {
                getComments(
                    postId = action.postId,
                    isPaging = true
                )
            }
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
                    isLoading = !isPaging && !shouldRefresh,
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
        _state.update { it.copy(isLoading = false, error = error, isRefreshing = false, isPaging = false) }
    }

    // Comment part
    private fun getComments(
        postId: String,
        isPaging: Boolean = false
    ) {
        val currentState = _commentState.value

        // Prevent duplicate pagination requests.
        if (isPaging && currentState.isPaging) return

        // No more pages available.
        if (isPaging && currentState.cursor.isNullOrBlank()) return

        val requestCursor = if (isPaging) {
            currentState.cursor.orEmpty()
        } else {
            ""
        }

        viewModelScope.launch {

            _commentState.update {
                it.copy(
                    isLoading = !isPaging,
                    isPaging = isPaging,
                    error = null
                )
            }

            postRepository.getComments(
                postId = postId,
                size = 10,
                cursor = requestCursor
            ).onSuccess { (comments, cursor, commentsCount) ->

                _commentState.update {
                    it.copy(
                        isLoading = false,
                        isPaging = false,
                        comments = if (isPaging) {
                            it.comments + comments
                        } else {
                            comments
                        },
                        cursor = cursor,
                        commentsCount = "$commentsCount"
                    )
                }

            }.onError { _ ->

                _commentState.update {
                    it.copy(
                        isLoading = false,
                        isPaging = false,
                        error = if (isPaging && it.comments.isNotEmpty()) {
                            null
                        } else {
                            "Failed to load comments"
                        }
                    )
                }
            }
        }
    }

    private fun doComment(postId: String, author: String) {
        viewModelScope.launch {
            val currentState = _commentState.value
            val optimisticComments = currentState.optimisticComments
            val optimisticComment = OptimisticComment(
                sort = optimisticComments.count() + 1,
                author = author,
                content = currentState.comment,
                updatedAt = "Sending"
            )

            val newOptimisticComments = optimisticComments + optimisticComment

            _commentState.update { it.copy(optimisticComments = newOptimisticComments.sortedByDescending { oc -> oc.sort }, comment = "") }
            _doCommentEvent.send(PostCommentEvent.OnCommentAdded)

            postRepository.doComment(
                postId = postId,
                content = currentState.comment
            ).onSuccess { (comment, commentsCount) ->
                _commentState.update {
                    it.copy(
                        optimisticComments = it.optimisticComments.map { c ->
                            if (c.id == optimisticComment.id) {
                                c.copy(
                                    id = comment.id,
                                    author =comment.authorName,
                                    content = comment.content,
                                    updatedAt = comment.updatedAt
                                )
                            } else {c}
                        },
                    )
                }

                _state.update {
                    it.copy(
                        posts = it.posts.map { post ->
                            if (post.id == postId) {
                                post.copy(commentsCount = commentsCount)
                            } else {
                                post
                            }
                        }
                    )
                }
            }.onError { ex ->
                _commentState.update {
                    it.copy(
                        optimisticComments = optimisticComments,
                        comment = currentState.comment // Let's restore the comment the poor dev wasted energy typing
                    )
                }
                when(ex) {
                    GenericError.UNKNOWN -> _doCommentEvent.send(PostCommentEvent.ShowError("Failed to post comment."))
                    GenericError.NO_INTERNET -> _doCommentEvent.send(PostCommentEvent.ShowError("No internet connection."))
                }
            }
        }
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







































