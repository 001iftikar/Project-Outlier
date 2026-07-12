package com.iftikar.outlier.feature.home.impl

import com.iftikar.outlier.core.models.Post

data class HomeScreenState(
    val posts: List<Post> = emptyList(),
    val imageIndex: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface HomeScreenAction {
    data object OnRetry : HomeScreenAction
}