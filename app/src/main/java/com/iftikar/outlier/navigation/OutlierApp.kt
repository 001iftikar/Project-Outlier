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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
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
import kotlinx.coroutines.launch

@Composable
fun OutlierApp() {
    val sharedVM = hiltViewModel<SharedViewModel>()
    val drawerUserInfoState = sharedVM.drawerUserInfoState
    val sessionViewModel = hiltViewModel<SessionViewModel>()
    val startDestination by sessionViewModel.startDestination.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val showGlobalSnackbar: (String) -> Unit = { message ->
        scope.launch {
            snackbarHostState.showSnackbar(message = message)
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
        val backStack = rememberNavBackStack(startDestination ?: LoginNavKey)
        Box(Modifier.fillMaxSize())
        {
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator()
                ),
                entryProvider = entryProvider {
                    emailVerificationEntry(backStack = backStack, navigateToHome = {
                        backStack.add(HomeNavKey)
                    })
                    registerEntry(backStack)
                    loginEntry(backStack)
                    homeEntry(
                        drawerUserInfoState = drawerUserInfoState,
                        backStack = backStack,
                        onRefetchClick = { sharedVM.fetchDrawerUserInfo() }
                    )
                    inboxEntry()
                    postEntry(
                        backStack = backStack,
                        showSnackbarOnSuccess = showGlobalSnackbar
                    )
                },
                transitionSpec = {
                    // Slide in from right when navigating forward
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(500)
                    ) togetherWith slideOutHorizontally(
                        targetOffsetX = { -it },
                        animationSpec = tween(500)
                    )
                },
                popTransitionSpec = {
                    // Slide in from left when navigating back
                    slideInHorizontally(
                        initialOffsetX = { -it },
                        animationSpec = tween(500)
                    ) togetherWith slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(500)
                    )
                },
                predictivePopTransitionSpec = {
                    // Slide in from left when navigating back
                    slideInHorizontally(
                        initialOffsetX = { -it },
                        animationSpec = tween(500)
                    ) togetherWith slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(500)
                    )
                }
            )
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .imePadding()
            )
        }
    }
}