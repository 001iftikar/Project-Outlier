package com.iftikar.outlier.feature.post.impl.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.iftikar.outlier.feature.post.impl.PostScreen
import com.iftikar.outlier.feature.post.api.PostNavKey
import com.iftikar.outlier.feature.post.impl.PostViewModel

fun EntryProviderScope<NavKey>.postEntry() {
    entry<PostNavKey> {
        val viewModel = hiltViewModel<PostViewModel>()
        PostScreen(
            viewModel = viewModel
        )
    }
}