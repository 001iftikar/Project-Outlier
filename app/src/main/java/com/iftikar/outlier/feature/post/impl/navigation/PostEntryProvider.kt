package com.iftikar.outlier.feature.post.impl.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.iftikar.outlier.feature.post.impl.PostScreen
import com.iftikar.outlier.feature.post.api.PostNavKey
import com.iftikar.outlier.feature.post.impl.PostViewModel

fun EntryProviderScope<NavKey>.postEntry(
    backStack: NavBackStack<NavKey>,
    showSnackbarOnSuccess: (String) -> Unit
) {
    entry<PostNavKey> {
        val viewModel = hiltViewModel<PostViewModel>()
        PostScreen(
            viewModel = viewModel,
            onSuccess = { message ->
                backStack.removeLastOrNull()
                showSnackbarOnSuccess(message)
            },
        )
    }
}