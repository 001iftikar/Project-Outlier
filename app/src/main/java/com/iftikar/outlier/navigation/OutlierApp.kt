package com.iftikar.outlier.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.iftikar.outlier.core.datastore.SessionViewModel
import com.iftikar.outlier.feature.auth.api.LoginNavKey
import com.iftikar.outlier.feature.auth.impl.navigation.emailVerificationEntry
import com.iftikar.outlier.feature.auth.impl.navigation.loginEntry
import com.iftikar.outlier.feature.auth.impl.navigation.registerEntry
import com.iftikar.outlier.feature.home.api.HomeNavKey
import com.iftikar.outlier.feature.home.impl.navigation.homeEntry
import com.iftikar.outlier.feature.inbox.impl.navigation.inboxEntry
import com.iftikar.outlier.feature.post.impl.navigation.postEntry
import com.iftikar.outlier.feature.shared.SharedViewModel
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

@Composable
fun OutlierApp(
    sharedViewModel: SharedViewModel
) {
    val drawerUserInfoState = sharedViewModel.drawerUserInfoState

    val sessionViewModel = hiltViewModel<SessionViewModel>()

    val startDestination by sessionViewModel.startDestination.collectAsStateWithLifecycle()

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val homeReselectEvents = remember {
        MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    }

    val scope = rememberCoroutineScope()

    val showGlobalSnackbar: (String) -> Unit = { message ->
        scope.launch {
            snackbarHostState.showSnackbar(
                message = message
            )
        }
    }

    if (startDestination == null) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

    } else {

        val backStack = rememberNavBackStack(
            startDestination ?: LoginNavKey
        )

        // Shared home feed state.
        val homeListState = rememberLazyListState()

        // Haze state used by the bottom navigation bar.
        val hazeState = rememberHazeState()

        // The currently selected main tab.
        val currentTab = backStack.lastOrNull() as? MainTabNavKey

        // Controls whether the bottom bar should be shown.
        var isScrollingUp by remember {
            mutableStateOf(true)
        }

        /*
         * Memento-style scroll direction detection.
         *
         * Scrolling down -> Hide bottom bar.
         * Scrolling up   -> Show bottom bar.
         *
         * Reset visibility whenever the main tab changes.
         */
        LaunchedEffect(homeListState, currentTab) {

            isScrollingUp = true

            if (currentTab == null) {
                return@LaunchedEffect
            }

            var previousIndex = homeListState.firstVisibleItemIndex

            var previousScrollOffset =
                homeListState.firstVisibleItemScrollOffset

            snapshotFlow {
                homeListState.firstVisibleItemIndex to
                        homeListState.firstVisibleItemScrollOffset
            }
                .drop(1)
                .collect { (index, offset) ->

                    if (index == 0 && offset == 0) {

                        isScrollingUp = true

                    } else if (index != previousIndex) {

                        isScrollingUp = index < previousIndex

                    } else {

                        isScrollingUp = offset < previousScrollOffset
                    }

                    previousIndex = index
                    previousScrollOffset = offset
                }
        }

        val bottomBarVisible =
            currentTab != null && isScrollingUp

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {

            NavDisplay(
                modifier = Modifier
                    .fillMaxSize()
                    .hazeSource(hazeState),
                backStack = backStack,

                onBack = {
                    sharedViewModel.setShouldSavePostState(false)

                    if (backStack.size > 1) {
                        backStack.removeLastOrNull()
                    }
                },

                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator()
                ),

                entryProvider = entryProvider {

                    emailVerificationEntry(
                        backStack = backStack,
                        navigateToHome = {
                            navigateToTab(
                                backStack,
                                HomeNavKey,
                                onHomeReselected = {}
                            )
                        }
                    )

                    registerEntry(backStack)

                    loginEntry(backStack)

                    homeEntry(
                        drawerUserInfoState = drawerUserInfoState,
                        listState = homeListState,
                        backStack = backStack,
                        homeReselectEvents = homeReselectEvents,
                        showErrorOnSnackBar = { message ->
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = message
                                )
                            }
                        }
                    )

//                    searchEntry()

                    inboxEntry()

//                    profileEntry()

                    postEntry(
                        backStack = backStack,
                        showSnackbarOnSuccess = showGlobalSnackbar,
                        sharedViewModel = sharedViewModel
                    )
                },

                transitionSpec = {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(500)
                    ) togetherWith slideOutHorizontally(
                        targetOffsetX = { -it },
                        animationSpec = tween(500)
                    )
                },

                popTransitionSpec = {
                    slideInHorizontally(
                        initialOffsetX = { -it },
                        animationSpec = tween(500)
                    ) togetherWith slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(500)
                    )
                },

                predictivePopTransitionSpec = {
                    slideInHorizontally(
                        initialOffsetX = { -it },
                        animationSpec = tween(500)
                    ) togetherWith slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(500)
                    )
                }
            )

            // Bottom navigation bar.
            if (currentTab != null) {

                OutlierBottomNavBar(
                    visible = bottomBarVisible,
                    selectedTab = currentTab,
                    hazeState = hazeState,
                    onTabSelected = { destination ->
                        navigateToTab(
                            backStack = backStack,
                            destination = destination,
                            onHomeReselected = {
                                homeReselectEvents.tryEmit(Unit)
                            }
                        )
                    },
                    modifier = Modifier.align(
                        Alignment.BottomCenter
                    )
                )
            }

            // Keep snackbars above the bottom navigation bar.
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(
                        bottom = if (bottomBarVisible) {
                            100.dp
                        } else {
                            0.dp
                        }
                    )
            )
        }
    }
}

/**
 * Switches between top-level tabs.
 *
 * Clears nested destinations before adding the selected tab.
 */
private fun navigateToTab(
    backStack: NavBackStack<NavKey>,
    destination: MainTabNavKey,
    onHomeReselected: () -> Unit
) {
    if (backStack.lastOrNull() == destination) {
        if (destination == HomeNavKey) {
            onHomeReselected()
        }
        return
    }

    if (backStack.contains(destination)) {
        backStack.remove(destination)
    }

//    backStack.clear()
    backStack.add(destination)
}