package com.iftikar.outlier.feature.home.impl.navigation

import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.iftikar.outlier.feature.home.api.HomeNavKey
import com.iftikar.outlier.feature.home.impl.HomeScreen
import com.iftikar.outlier.feature.home.impl.HomeViewModel
import com.iftikar.outlier.feature.inbox.api.InboxNavKey
import com.iftikar.outlier.feature.post.api.PostNavKey
import com.iftikar.outlier.feature.shared.DrawerUserInfoState
import com.iftikar.outlier.feature.shared.SharedViewModel
import kotlinx.coroutines.flow.StateFlow

fun EntryProviderScope<NavKey>.homeEntry(
    drawerUserInfoState: StateFlow<DrawerUserInfoState>,
    onRefetchClick: () -> Unit,
    backStack: NavBackStack<NavKey>
) {
    entry<HomeNavKey> {
        val viewModel = hiltViewModel<HomeViewModel>()
        val state by drawerUserInfoState.collectAsStateWithLifecycle()
        HomeScreen(
            drawerUserInfoState = state,
            viewModel = viewModel,
            onDrawerItemClick = { navKey ->
                when (navKey) {
                    InboxNavKey -> {
                        backStack.add(navKey)
                    }

                    PostNavKey -> {
                        backStack.add(navKey)
                    }
                }
            },
            onRefetchClick = onRefetchClick
        )
    }
}