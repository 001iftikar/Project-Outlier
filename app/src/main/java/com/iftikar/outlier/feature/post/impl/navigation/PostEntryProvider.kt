package com.iftikar.outlier.feature.post.impl.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.iftikar.outlier.feature.post.api.PostNavKey
import com.iftikar.outlier.feature.post.impl.PostScreen
import com.iftikar.outlier.feature.post.impl.PostViewModel
import com.iftikar.outlier.feature.shared.SharedViewModel

fun EntryProviderScope<NavKey>.postEntry(
    backStack: NavBackStack<NavKey>,
    sharedViewModel: SharedViewModel,
    showSnackbarOnSuccess: (String) -> Unit
) {
    entry<PostNavKey> { navKey ->
        val viewModel = hiltViewModel<PostViewModel, PostViewModel.Factory>(
            creationCallback = { factory ->
                factory.create(
                    navKey
                )
            }
        )
        LaunchedEffect(Unit) {
            sharedViewModel.setShouldSavePostState(true) {
                val index = backStack.lastIndex
                if (index != -1) {
                    val currentKey = backStack[index]
                    if (currentKey is PostNavKey) {
                        backStack[index] = currentKey.copy(
                            stateToSurvive = viewModel.state.value.stateToSurvive
                        )
                    }
                }
            }
        }
        PostScreen(
            viewModel = viewModel,
            onSuccess = { message ->
                backStack.removeLastOrNull()
                showSnackbarOnSuccess(message)
                sharedViewModel.setShouldSavePostState(false)
            }
        )
    }
}