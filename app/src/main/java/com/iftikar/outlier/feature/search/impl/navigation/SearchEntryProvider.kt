package com.iftikar.outlier.feature.search.impl.navigation

import androidx.compose.foundation.lazy.LazyListState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.iftikar.outlier.feature.search.api.SearchNavKey
import com.iftikar.outlier.feature.search.impl.SearchScreen
import com.iftikar.outlier.feature.search.impl.SearchViewModel
import kotlinx.coroutines.flow.SharedFlow

fun EntryProviderScope<NavKey>.searchEntry(
    noSearchListState: LazyListState,
    searchListState: LazyListState,
    assignSearchListState: (Boolean) -> Unit,
    searchReselectEvent: SharedFlow<Unit>
) {
    entry<SearchNavKey> {
        val viewModel = hiltViewModel<SearchViewModel>()
        SearchScreen(
            viewModel = viewModel,
            noSearchListState = noSearchListState,
            searchListState = searchListState,
            assignSearchListState = assignSearchListState,
            searchReselectEvent = searchReselectEvent
        )
    }
}