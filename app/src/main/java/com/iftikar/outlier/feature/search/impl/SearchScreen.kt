package com.iftikar.outlier.feature.search.impl

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iftikar.outlier.R
import com.iftikar.outlier.core.designsystem.component.post.EmptyPostsComponent
import com.iftikar.outlier.core.designsystem.component.post.PostComponent
import com.iftikar.outlier.core.designsystem.component.post.ProjectMainContent
import com.iftikar.outlier.core.designsystem.component.shimmer.ShimmerBlock
import com.iftikar.outlier.core.designsystem.component.shimmer.rememberShimmerBrush
import com.iftikar.outlier.core.designsystem.theme.LocalSpacing
import com.iftikar.outlier.feature.search.components.SearchTopBar
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    noSearchListState: LazyListState,
    searchListState: LazyListState,
    assignSearchListState: (Boolean) -> Unit,
    searchReselectEvent: SharedFlow<Unit>
) {
    val spacing = LocalSpacing.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val action = viewModel::onAction

    val refreshState = rememberPullToRefreshState()
    val pullProgress by remember(refreshState) {
        derivedStateOf {
            refreshState.distanceFraction.coerceIn(0f, 1f)
        }
    }

    val shouldShowIndicator =
        pullProgress > 0f || state.isRefreshing

    val indicatorHeight by animateDpAsState(
        targetValue = if (shouldShowIndicator) 80.dp else 0.dp,
        animationSpec = tween(
            durationMillis = 250,
            easing = FastOutSlowInEasing
        ),
        label = "pullToRefreshIndicatorHeight"
    )

    LaunchedEffect(state.shouldShowSearchedResult) {
        if (state.shouldShowSearchedResult) {
            assignSearchListState(true)
        } else {
            assignSearchListState(false)
        }
    }

    val listState = if (state.shouldShowSearchedResult) searchListState else noSearchListState

    LaunchedEffect(
        listState,
        state.shouldShowSearchedResult,
        state.posts.size,
        state.cursor,
        state.searchResult?.posts?.size,
        state.searchResult?.matchedTechStackInstead?.size,
        state.searchResult?.nextCursor,
        state.searchResult?.hasMore,
        state.searchResult?.nextMatchedTechStackInsteadCursor,
        state.searchResult?.hasMoreMatchedTechStackInstead,
        state.isLoading,
        state.isSearchLoading,
        state.isPaging
    ) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo

            val lastVisibleItem = layoutInfo.visibleItemsInfo
                .lastOrNull()?.index ?: -1

            val totalItems = layoutInfo.totalItemsCount

            totalItems > 0 &&
                    lastVisibleItem >= (totalItems - 3).coerceAtLeast(0)
        }
            .distinctUntilChanged()
            .collect { isNearEnd ->

                if (!isNearEnd || state.isPaging) {
                    return@collect
                }

                if (state.shouldShowSearchedResult) {

                    val result = state.searchResult
                        ?: return@collect

                    val canLoadMorePosts =
                        result.hasMore &&
                                !result.nextCursor.isNullOrBlank()

                    val canLoadMoreTechStack =
                        result.hasMoreMatchedTechStackInstead &&
                                !result.nextMatchedTechStackInsteadCursor.isNullOrBlank()

                    if (
                        !state.isSearchLoading &&
                        (canLoadMorePosts || canLoadMoreTechStack)
                    ) {
                        action(SearchScreenAction.OnSearchPaging)
                    }

                } else {

                    if (
                        state.posts.isNotEmpty() &&
                        !state.isLoading &&
                        !state.cursor.isNullOrBlank()
                    ) {
                        action(SearchScreenAction.OnPaging)
                    }
                }
            }
    }

    LaunchedEffect(viewModel, searchReselectEvent) {
        searchReselectEvent.collect {

            // Don't start another refresh if one is already running.
            if (viewModel.state.value.isRefreshing) {
                return@collect
            }

            // Scroll to the beginning.
            if (noSearchListState.layoutInfo.totalItemsCount > 0) {
                noSearchListState.animateScrollToItem(0)
            }

            // Simulate pulling to the refresh threshold.
            refreshState.animateToThreshold()

            // Trigger the actual network request.
            action(SearchScreenAction.OnRefresh)
        }
    }

    Scaffold(
        topBar = {
            SearchTopBar(
                modifier = Modifier.fillMaxWidth(),
                query = state.query,
                onQueryChange = { action(SearchScreenAction.OnQueryChange(it)) },
                onDone = { action(SearchScreenAction.OnSearch) },
                shouldShowBack = state.shouldShowSearchedResult,
                onBackClick = { action(SearchScreenAction.OnBackClick) }
            )
        }
    ) { innerPadding ->
        if (!state.shouldShowSearchedResult) {
            UnsearchedResult(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                state = state,
                indicatorHeight = indicatorHeight,
                shouldShowIndicator = shouldShowIndicator,
                pullProgress = pullProgress,
                listState = listState,
                refreshState = refreshState,
                spacing = spacing,
                action = action
            )
        } else {
            SearchedResult(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                state = state,
                listState = listState,
                spacing = spacing,
                action = action
            )
        }
    }
}

@Composable
private fun UnsearchedResult(
    modifier: Modifier = Modifier,
    state: SearchScreenState,
    indicatorHeight: Dp,
    shouldShowIndicator: Boolean,
    pullProgress: Float,
    listState: LazyListState,
    refreshState: PullToRefreshState,
    spacing: com.iftikar.outlier.core.designsystem.theme.PaddingValues,
    action: (SearchScreenAction) -> Unit
) {
    if (state.isLoading) {
        LazyColumn(
            modifier = modifier.testTag("home_posts_shimmer"),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            userScrollEnabled = false,
            contentPadding = PaddingValues(
                horizontal = spacing.horizontalPadding,
                vertical = 8.dp
            )
        ) {
            items(5) {
                ShimmerBlock(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f),
                    brush = rememberShimmerBrush(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    } else {
        if (state.error != null) {
            Box(
                modifier = modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { action(SearchScreenAction.OnRetry) }
                ) {
                    Text(
                        text = "Try Again"
                    )
                }
            }
        } else {
            Box(
                modifier = modifier
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(indicatorHeight)
                            .clipToBounds(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (shouldShowIndicator || indicatorHeight > 0.dp) {

                            if (state.isRefreshing) {

                                // Actual refresh: indeterminate spinning indicator.
                                CircularProgressIndicator(
                                    modifier = Modifier.size(30.dp),
                                    strokeWidth = 3.dp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )

                            } else {

                                // User is dragging: progress follows drag distance.
                                CircularProgressIndicator(
                                    progress = {
                                        pullProgress
                                    },
                                    modifier = Modifier.size(30.dp),
                                    strokeWidth = 3.dp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .pullToRefresh(
                                isRefreshing = state.isRefreshing,
                                state = refreshState,
                                threshold = state.pullToRefreshThreshold,
                                onRefresh = {
                                    action(SearchScreenAction.OnRefresh)
                                }
                            )
                            .testTag("home_posts")
                            .padding(horizontal = spacing.horizontalPadding),
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        if (state.posts.isEmpty()) {
                            item {
                                EmptyPostsComponent(
                                    image = R.drawable.empty_cat,
                                    modifier = Modifier.fillParentMaxSize()
                                )
                            }
                        } else {
                            items(
                                state.posts,
                                key = { it.id }
                            ) { post ->

                                val cardShape = RoundedCornerShape(16.dp)

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(cardShape)
                                        .border(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.outline.copy(
                                                alpha = 0.35f
                                            ),
                                            shape = cardShape
                                        )
                                        .padding(10.dp)
                                ) {
                                    ProjectMainContent(
                                        projectImages = post.images,
                                        projectTitle = post.title
                                    )
                                }
                            }

                            // Paging indicator remains outside the card border.
                            if (state.isPaging) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchedResult(
    modifier: Modifier = Modifier,
    state: SearchScreenState,
    listState: LazyListState,
    spacing: com.iftikar.outlier.core.designsystem.theme.PaddingValues,
    action: (SearchScreenAction) -> Unit
) {
    if (state.isSearchLoading) {
        LazyColumn(
            modifier = modifier.testTag("home_posts_shimmer"),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            userScrollEnabled = false,
            contentPadding = PaddingValues(
                horizontal = spacing.horizontalPadding,
                vertical = 8.dp
            )
        ) {
            items(5) {
                ShimmerBlock(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f),
                    brush = rememberShimmerBrush(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    } else {
        if (state.error != null) {
            Box(
                modifier = modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { action(SearchScreenAction.OnRetry) }
                ) {
                    Text(
                        text = "Try Again"
                    )
                }
            }
        } else {
            state.searchResult?.let { result ->
                LazyColumn(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = spacing.horizontalPadding
                        ),
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    if (result.posts.isEmpty()) {
                        item {
                            EmptyPostsComponent(
                                image = R.drawable.empty_cat,
                                modifier = Modifier.fillParentMaxSize()
                            )
                        }
                    } else {
                        items(result.posts, key = { it.id }) { post ->
                            PostComponent(
                                post = post,
                                showOnSearchResult = true
                            )
                        }
                        if (state.isPaging) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}