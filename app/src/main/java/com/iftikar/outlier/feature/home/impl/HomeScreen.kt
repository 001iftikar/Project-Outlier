package com.iftikar.outlier.feature.home.impl

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.iftikar.outlier.core.designsystem.theme.LocalSpacing
import com.iftikar.outlier.feature.home.components.bars.HomeTopBar
import com.iftikar.outlier.feature.home.components.drawer.DrawerContent
import com.iftikar.outlier.feature.home.components.post.PostComponent
import com.iftikar.outlier.feature.home.components.post.PostFeedShimmer
import com.iftikar.outlier.feature.shared.DrawerUserInfoState
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    drawerUserInfoState: DrawerUserInfoState,
    viewModel: HomeViewModel,
    onDrawerItemClick: (NavKey) -> Unit,
    onRefetchClick: () -> Unit,
    onLogoutSuccess: () -> Unit,
    showErrorOnSnackBar: (String) -> Unit
) {
    val spacing = LocalSpacing.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val action = viewModel::onAction
    val postReact = viewModel::onPostReact
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrimAlpha by animateFloatAsState(
        targetValue = if (drawerState.targetValue == DrawerValue.Open) 0.3f else 0.0f,
        label = "Scrim Alpha"
    )
    val topbarScrollBehaviour = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val refreshState = rememberPullToRefreshState()
    val shouldShowIndicator =
        refreshState.distanceFraction != 0f && refreshState.distanceFraction < state.pullToRefreshThreshold.value
    val indicatorHeight by animateDpAsState(
        targetValue = if (shouldShowIndicator) 80.dp else 0.dp,
        animationSpec = tween(
            durationMillis = 250,
            easing = FastOutSlowInEasing
        ),
        label = "pullToRefreshIndicatorHeight"
    )
    val lazyListState = rememberLazyListState()
    LaunchedEffect(
        lazyListState,
        state.posts.size,
        state.cursor,
        state.isLoading,
        state.isPaging
    ) {
        snapshotFlow {
            val layoutInfo = lazyListState.layoutInfo

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
    DismissibleNavigationDrawer(
        modifier = Modifier
            .fillMaxSize(),
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                drawerUserInfoState = drawerUserInfoState,
                drawerState = drawerState,
                onDrawerItemClick = { navKey ->
                    onDrawerItemClick(navKey)
                    scope.launch { drawerState.close() }
                },
                onRefetchClick = onRefetchClick,
                onProfileClick = { action(HomeScreenAction.OnLogout) }
            )
        },
        gesturesEnabled = true
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .semantics {
                    testTagsAsResourceId = true
                }
                .nestedScroll(topbarScrollBehaviour.nestedScrollConnection)
                .drawWithContent {
                    drawContent()

                    if (scrimAlpha > 0f) {
                        drawRect(
                            color = Color.Black,
                            alpha = scrimAlpha
                        )
                    }
                }
                .clickable(
                    enabled = drawerState.targetValue == DrawerValue.Open,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                        }
                    },
                    interactionSource = remember {
                        MutableInteractionSource()
                    },
                    indication = null
                ),
            contentWindowInsets = ScaffoldDefaults.contentWindowInsets.exclude(WindowInsets.statusBars),
            topBar = {
                HomeTopBar(
                    drawerState = drawerState,
                    onMenuClick = {
                        if (drawerState.isClosed) {
                            scope.launch { drawerState.open() }
                        } else {
                            scope.launch { drawerState.close() }
                        }
                    },
                    scrollBehavior = topbarScrollBehaviour
                )
            }) { innerPadding ->
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
                                    CircularProgressIndicator(
                                        progress = {
                                            refreshState.distanceFraction.coerceIn(0f, 1f)
                                        },
                                        modifier = Modifier.size(30.dp),
                                        strokeWidth = 3.dp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
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
                                state = lazyListState,
                                verticalArrangement = Arrangement.spacedBy(18.dp)
                            ) {
                                items(
                                    state.posts,
                                    key = { it.id }
                                ) { post ->
                                    PostComponent(
                                        post = post,
                                        drawerState = drawerState,
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
    }
}