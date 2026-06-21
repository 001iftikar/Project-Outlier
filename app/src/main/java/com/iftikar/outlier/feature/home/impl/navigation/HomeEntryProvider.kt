package com.iftikar.outlier.feature.home.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.iftikar.outlier.feature.home.api.HomeNavKey
import com.iftikar.outlier.feature.home.impl.HomeScreen

fun EntryProviderScope<NavKey>.homeEntry() {
    entry<HomeNavKey> {
        HomeScreen()
    }
}