package com.iftikar.outlier.feature.auth.impl.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.iftikar.outlier.feature.auth.api.CreateUserNavKey
import com.iftikar.outlier.feature.auth.impl.CreateUserScreen
import com.iftikar.outlier.feature.auth.impl.CreateUserViewModel
import com.iftikar.outlier.feature.home.api.HomeNavKey

fun EntryProviderScope<NavKey>.createUserEntry(
    backStack: NavBackStack<NavKey>
) {
    entry<CreateUserNavKey> { navKey ->
        val viewModel = hiltViewModel<CreateUserViewModel, CreateUserViewModel.Factory>(
            creationCallback = { factory ->
                factory.create(navKey)
            }
        )
        CreateUserScreen(viewModel = viewModel,
            onSuccess = {
                backStack.clear()
                backStack.add(HomeNavKey)
            })
    }
}