package com.iftikar.outlier.feature.auth.impl.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.iftikar.outlier.feature.auth.api.EmailVerifyNavKey
import com.iftikar.outlier.feature.auth.api.LoginNavKey
import com.iftikar.outlier.feature.auth.api.RegisterNavKey
import com.iftikar.outlier.feature.auth.impl.EmailVerificationScreen
import com.iftikar.outlier.feature.auth.impl.EmailVerificationViewModel

fun EntryProviderScope<NavKey>.emailVerificationEntry(
    backStack: NavBackStack<NavKey>
) {
    entry<EmailVerifyNavKey> {
        val viewModel = hiltViewModel<EmailVerificationViewModel>()
        EmailVerificationScreen(
            viewModel = viewModel,
            onAlreadyUserClick = {
                backStack.clear()
                backStack.add(LoginNavKey)
            },
            onSuccess = { email ->
                backStack.add(RegisterNavKey(email))
            })
    }
}