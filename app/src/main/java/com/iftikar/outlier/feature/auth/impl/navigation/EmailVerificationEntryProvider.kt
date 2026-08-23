package com.iftikar.outlier.feature.auth.impl.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.iftikar.outlier.feature.auth.api.EmailVerifyNavKey
import com.iftikar.outlier.feature.auth.impl.EmailVerificationScreen
import com.iftikar.outlier.feature.auth.impl.EmailVerificationViewModel

fun EntryProviderScope<NavKey>.emailVerificationEntry(
    backStack: NavBackStack<NavKey>,
    navigateToHome: () -> Unit
) {
    entry<EmailVerifyNavKey> { navKey ->
        val viewModel = hiltViewModel<EmailVerificationViewModel, EmailVerificationViewModel.Factory>(
            creationCallback = { factory ->
                factory.create(navKey)
            }
        )

        EmailVerificationScreen(
            viewModel = viewModel,
            onBackToRegisterClick = {backStack.removeLastOrNull()},
            onSuccess = {
                backStack.clear()
                navigateToHome()
            }
        )
    }
}