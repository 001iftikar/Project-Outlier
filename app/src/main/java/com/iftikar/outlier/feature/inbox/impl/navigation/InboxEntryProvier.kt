package com.iftikar.outlier.feature.inbox.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.iftikar.outlier.feature.inbox.InboxScreen
import com.iftikar.outlier.feature.inbox.api.InboxNavKey

fun EntryProviderScope<NavKey>.inboxEntry() {
    entry<InboxNavKey> {
        InboxScreen()
    }
}