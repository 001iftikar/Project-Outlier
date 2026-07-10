package com.iftikar.outlier.feature.post.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.iftikar.outlier.feature.post.PostScreen
import com.iftikar.outlier.feature.post.api.PostNavKey

fun EntryProviderScope<NavKey>.postEntry() {
    entry<PostNavKey> {
        PostScreen()
    }
}