package com.iftikar.outlier.feature.auth.impl.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.iftikar.outlier.feature.auth.api.CreateUserNavKey
import com.iftikar.outlier.feature.auth.api.EmailVerifyNavKey
import com.iftikar.outlier.feature.auth.api.RegisterNavKey
import com.iftikar.outlier.feature.auth.impl.RegisterScreen
import com.iftikar.outlier.feature.auth.impl.RegisterViewModel

fun EntryProviderScope<NavKey>.registerEntry(
    backStack: NavBackStack<NavKey>
) {
    entry<RegisterNavKey> { navKey ->
        val viewModel = hiltViewModel<RegisterViewModel>()
        RegisterScreen(
            viewModel = viewModel,
            onSuccess = { email ->
                backStack.add(EmailVerifyNavKey(email))
            }
        )
    }
}