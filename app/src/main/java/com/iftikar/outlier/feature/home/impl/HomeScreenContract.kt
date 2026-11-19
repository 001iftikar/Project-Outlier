package com.iftikar.outlier.feature.home.impl

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.iftikar.outlier.core.models.Post

data class HomeScreenState(
    val posts: List<Post> = emptyList(),
    val cursor: String? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val pullToRefreshThreshold: Dp = 30.dp,
    val isPaging: Boolean = false,
    val error: String? = null,
    val commentToOpen: String? = null
)

sealed interface HomeScreenAction {
    data object OnRetry : HomeScreenAction
    data object OnRefresh : HomeScreenAction
    data object OnPaging : HomeScreenAction
    data object OnLogout: HomeScreenAction
    data object OnCommentDismiss: HomeScreenAction
}

sealed interface PostReactionAction {
    data class OnLikeClick(val postId: String) : PostReactionAction
    data class OnUnlikeClick(val postId: String) : PostReactionAction
    data class OnCommentClick(val postId: String) : PostReactionAction
}

sealed interface HomeScreenEvent {
    data object OnSuccessLogout : HomeScreenEvent
    data class ShowSnackBarError(val err: String) : HomeScreenEvent
//    data object OnRefreshTriggeredByHomePress : HomeScreenEvent
}