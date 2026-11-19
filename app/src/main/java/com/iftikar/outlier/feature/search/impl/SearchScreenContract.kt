package com.iftikar.outlier.feature.search.impl

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.iftikar.outlier.core.models.Post
import com.iftikar.outlier.core.models.SearchResult

data class SearchScreenState(
    val query: String = "",
    val posts: List<Post> = emptyList(),
    val shouldShowSearchedResult: Boolean = false,
    val searchResult: SearchResult? = null,
    val cursor: String? = null,
    val isLoading: Boolean = false,
    val isSearchLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val pullToRefreshThreshold: Dp = 30.dp,
    val isPaging: Boolean = false,
    val error: String? = null,
)

sealed interface SearchScreenAction {
    data class OnQueryChange(val query: String) : SearchScreenAction
    data object OnSearch : SearchScreenAction
    data object OnRetry : SearchScreenAction
    data object OnRefresh : SearchScreenAction
    data object OnPaging : SearchScreenAction
    data object OnSearchPaging : SearchScreenAction
    data object OnBackClick : SearchScreenAction
}