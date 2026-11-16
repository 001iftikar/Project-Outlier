package com.iftikar.outlier.feature.home.impl

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.iftikar.outlier.core.models.HomePosts

data class HomeScreenState(
    val posts: HomePosts = HomePosts(),
    val imageIndex: Int = 0,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val pullToRefreshThreshold: Dp = 30.dp,
    val error: String? = null
)

sealed interface HomeScreenAction {
    data object OnRetry : HomeScreenAction
    data object OnRefresh : HomeScreenAction
    data object OnLogout: HomeScreenAction
}

sealed interface PostReactionAction {
    data class OnLikeClick(val postId: String) : PostReactionAction
    data class OnUnlikeClick(val postId: String) : PostReactionAction
    data class OnCommentClick(val postId: String) : PostReactionAction
}

sealed interface HomeScreenEvent {
    data object OnSuccessLogout : HomeScreenEvent
    data class ShowSnackBarError(val err: String) : HomeScreenEvent
}