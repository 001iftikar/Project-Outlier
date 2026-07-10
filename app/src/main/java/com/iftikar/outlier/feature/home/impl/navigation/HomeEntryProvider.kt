package com.iftikar.outlier.feature.home.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.iftikar.outlier.feature.home.api.HomeNavKey
import com.iftikar.outlier.feature.home.impl.HomeScreen
import com.iftikar.outlier.feature.inbox.api.InboxNavKey
import com.iftikar.outlier.feature.post.api.PostNavKey

fun EntryProviderScope<NavKey>.homeEntry(
    backStack: NavBackStack<NavKey>
) {
    entry<HomeNavKey> {
        HomeScreen(
            onDrawerItemClick = { navKey ->
                when (navKey) {
                    InboxNavKey -> {
                        backStack.add(navKey)
                    }

                    PostNavKey -> {
                        backStack.add(navKey)
                    }
                }
            }
        )
    }
}