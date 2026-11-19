package com.iftikar.outlier.feature.search.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iftikar.outlier.core.domain.repository.PostRepository
import com.iftikar.outlier.core.result.GetPostError
import com.iftikar.outlier.core.result.onError
import com.iftikar.outlier.core.result.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {
    private val _state = MutableStateFlow(SearchScreenState())
    val state = _state.asStateFlow()

    init {
        getPosts()
    }

    fun onAction(action: SearchScreenAction) {
        when (action) {
            is SearchScreenAction.OnQueryChange -> {
                _state.update {
                    it.copy(query = action.query)
                }
            }

            SearchScreenAction.OnRetry -> {
                if (_state.value.shouldShowSearchedResult) {
                    searchPosts(isPaging = false)
                } else {
                    getPosts()
                }
            }

            SearchScreenAction.OnRefresh -> {
                _state.update {
                    it.copy(query = "")
                }
                getPosts("", true)
            }

            SearchScreenAction.OnPaging -> {
                getPosts(
                    cursor = _state.value.cursor ?: "",
                    isPaging = true
                )
            }

            SearchScreenAction.OnSearch -> {
                searchPosts(isPaging = false)
            }

            SearchScreenAction.OnSearchPaging -> {
                searchPosts(isPaging = true)
            }

            SearchScreenAction.OnBackClick -> {
                _state.update {
                    it.copy(
                        shouldShowSearchedResult = false,
                        searchResult = null,
                        query = "",
                        error = null,
                        isSearchLoading = false,
                        isPaging = false
                    )
                }
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
                    shouldShowSearchedResult = false,
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

    private fun searchPosts(
        isPaging: Boolean = false
    ) {
        val currentState = _state.value
        val query = currentState.query.trim()

        if (query.isBlank()) return

        if (isPaging) {
            if (
                currentState.isPaging ||
                currentState.isSearchLoading
            ) {
                return
            }
        }

        val previousResult = currentState.searchResult

        if (isPaging && previousResult == null) return

        viewModelScope.launch {

            _state.update {
                it.copy(
                    shouldShowSearchedResult = true,
                    error = null,
                    isPaging = isPaging,
                    isSearchLoading = !isPaging,
                    searchResult = if (isPaging) {
                        it.searchResult
                    } else {
                        null
                    }
                )
            }

            postRepository.searchPosts(
                query = query,
                size = 5,
                cursor = _state.value.searchResult?.nextCursor ?: "",
                matchedTechStackInsteadCursor = _state.value.searchResult?.nextMatchedTechStackInsteadCursor ?: "",
            ).onSuccess { newResult ->

                _state.update { current ->

                    val mergedResult = if (!isPaging) {
                        newResult
                    } else {
                        val previous = current.searchResult

                        if (previous == null) {
                            newResult
                        } else {
                            newResult.copy(
                                posts = (
                                        previous.posts + newResult.posts
                                        ).distinctBy { it.id },

                                matchedTechStackInstead = (
                                        previous.matchedTechStackInstead +
                                                newResult.matchedTechStackInstead
                                        ).distinctBy { it.id }
                            )
                        }
                    }

                    current.copy(
                        isSearchLoading = false,
                        isPaging = false,
                        error = null,
                        searchResult = mergedResult
                    )
                }

            }.onError { ex ->

                _state.update {
                    it.copy(
                        isSearchLoading = false,
                        isPaging = false
                    )
                }

                when (ex) {
                    GetPostError.NO_INTERNET ->
                        setError("You need to check the internet connection")

                    GetPostError.SERVER ->
                        setError("Oops! This is a server error")

                    GetPostError.UNKNOWN ->
                        setError("Oops! Something went wrong")

                    GetPostError.NO_DATA ->
                        setError("No data found")

                    GetPostError.DEVELOPER_MESSED_UP ->
                        setError("The developer messed up! Contact them.")
                }
            }
        }
    }

    private fun setError(error: String) {
        _state.update {
            it.copy(
                isLoading = false,
                error = error,
                isRefreshing = false,
                isPaging = false
            )
        }
    }
}