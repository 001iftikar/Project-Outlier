package com.iftikar.outlier.feature.home.impl

import com.iftikar.outlier.core.models.HomePosts

data class HomeScreenState(
    val posts: HomePosts = HomePosts(),
    val imageIndex: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface HomeScreenAction {
    data object OnRetry : HomeScreenAction
    data object OnLogout: HomeScreenAction
}

sealed interface HomeScreenEvent {
    data object OnSuccessLogout : HomeScreenEvent
}