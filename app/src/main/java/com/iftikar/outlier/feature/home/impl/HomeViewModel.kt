package com.iftikar.outlier.feature.home.impl

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
class HomeViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {
    private val _state = MutableStateFlow(HomeScreenState())
    val state = _state.asStateFlow()

    init {
//        getPosts()
    }

    fun onAction(action: HomeScreenAction) {
        when(action) {
            HomeScreenAction.OnRetry -> getPosts()
        }
    }
    private fun getPosts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            postRepository.getPosts().onSuccess { posts ->
                _state.update {
                    it.copy(isLoading = false, posts = posts)
                }
            }.onError { ex ->
                when(ex) {
                    GetPostError.NO_INTERNET -> setError("You need to check the internet connection")
                    GetPostError.SERVER -> setError("Oops! This is a server error")
                    GetPostError.UNKNOWN -> setError("Oops! Something went wrong")
                    GetPostError.NO_DATA -> setError("No data found")
                }
            }
        }
    }

    private fun setError(error: String) {
        _state.update { it.copy(isLoading = false, error = error) }
    }
}







































