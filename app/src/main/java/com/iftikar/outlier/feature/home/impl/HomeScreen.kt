package com.iftikar.outlier.feature.home.impl

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iftikar.outlier.core.designsystem.theme.LocalSpacing
import com.iftikar.outlier.feature.home.components.bars.HomeTopBar
import com.iftikar.outlier.feature.home.components.comment.CommentsBottomSheet
import com.iftikar.outlier.feature.home.components.post.PostComponent
import com.iftikar.outlier.feature.home.components.post.PostFeedShimmer
import com.iftikar.outlier.feature.shared.DrawerUserInfoState
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    drawerUserInfoState: DrawerUserInfoState,
    viewModel: HomeViewModel,
    listState: LazyListState,
    onCreatePostClick: () -> Unit,
    onLogoutSuccess: () -> Unit,
    homeReselectEvents: SharedFlow<Unit>,
    showErrorOnSnackBar: (String) -> Unit
) {
    val spacing = LocalSpacing.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val action = viewModel::onAction
    val postReact = viewModel::onPostReact

    val topbarScrollBehaviour = TopAppBarDefaults.enterAlwaysScrollBehavior()
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

    // Comment part
    val commentState by viewModel.commentState.collectAsStateWithLifecycle()
    val commentAction = viewModel::onPostCommentAction
    LaunchedEffect(
        listState,
        state.posts.size,
        state.cursor,
        state.isLoading,
        state.isPaging
    ) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo

            val lastVisibleItem = layoutInfo.visibleItemsInfo
                .lastOrNull()?.index ?: -1

            val totalItems = layoutInfo.totalItemsCount

            totalItems > 0 &&
                    lastVisibleItem >= (totalItems - 3).coerceAtLeast(0)
        }.distinctUntilChanged()
            .collect { isNearEnd ->

                if (
                    isNearEnd &&
                    state.posts.isNotEmpty() &&
                    !state.isLoading &&
                    !state.isPaging &&
                    !state.cursor.isNullOrBlank()
                ) {
                    action(HomeScreenAction.OnPaging)
                }
            }
    }
    LaunchedEffect(true) {
        viewModel.event.collect { event ->
            when (event) {
                HomeScreenEvent.OnSuccessLogout -> onLogoutSuccess()
                is HomeScreenEvent.ShowSnackBarError -> showErrorOnSnackBar(event.err)
            }
        }
    }

    LaunchedEffect(viewModel, homeReselectEvents) {
        homeReselectEvents.collect {

            // Don't start another refresh if one is already running.
            if (viewModel.state.value.isRefreshing) {
                return@collect
            }

            // Scroll to the beginning.
            if (listState.layoutInfo.totalItemsCount > 0) {
                listState.animateScrollToItem(0)
            }

            // Simulate pulling to the refresh threshold.
            refreshState.animateToThreshold()

            // Trigger the actual network request.
            action(HomeScreenAction.OnRefresh)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .semantics {
                testTagsAsResourceId = true
            }
            .nestedScroll(
                topbarScrollBehaviour.nestedScrollConnection
            ),
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets
            .exclude(WindowInsets.statusBars)
            .exclude(WindowInsets.navigationBars),
        topBar = {
            HomeTopBar(
                isDeveloper = drawerUserInfoState.drawerUserInfo?.isDeveloper == true,
                onCreatePostClick = onCreatePostClick,
                scrollBehavior = topbarScrollBehaviour
            )
        }
    ) { innerPadding ->
        if (state.isLoading) {
            PostFeedShimmer(
                contentPadding = innerPadding
            )
        } else {
            if (state.error != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = { action(HomeScreenAction.OnRetry) }
                    ) {
                        Text(
                            text = "Try Again"
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
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
                                        action(HomeScreenAction.OnRefresh)
                                    }
                                )
                                .testTag("home_posts")
                                .padding(
                                    horizontal = spacing.horizontalPadding
                                ),
                            state = listState,
                            verticalArrangement = Arrangement.spacedBy(18.dp)
                        ) {
                            items(
                                state.posts,
                                key = { it.id }
                            ) { post ->
                                PostComponent(
                                    post = post,
                                    onPostReactClick = postReact
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
    if (state.commentToOpen != null) {
        CommentsBottomSheet(
            comments = commentState.comments,
            optimisticComments = commentState.optimisticComments,

            isLoading = commentState.isLoading,
            isPaging = commentState.isPaging,
            hasMore = !commentState.cursor.isNullOrBlank(),

            error = commentState.error,

            commentText = commentState.comment,
            onCommentTextChange = {
                commentAction(PostCommentAction.OnCommentChange(it))
            },

            onSendComment = {
                commentAction(
                    PostCommentAction.DoComment(
                        state.commentToOpen!!,
                        drawerUserInfoState.drawerUserInfo?.name ?: "User"
                    )
                )
            },

            onLoadMore = {
                commentAction(
                    PostCommentAction.OnLoadMore(
                        state.commentToOpen!!
                    )
                )
            },

            onDismiss = {
                action(HomeScreenAction.OnCommentDismiss)
            },
            event = viewModel.doCommentEvent,
            onCommentAddedFailed = showErrorOnSnackBar
        )
    }

}