package com.iftikar.outlier.feature.auth.impl.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.iftikar.outlier.feature.auth.api.EmailVerifyNavKey
import com.iftikar.outlier.feature.auth.api.LoginNavKey
import com.iftikar.outlier.feature.auth.impl.LoginScreen
import com.iftikar.outlier.feature.auth.impl.LoginViewModel
import com.iftikar.outlier.feature.home.api.HomeNavKey

fun EntryProviderScope<NavKey>.loginEntry(
    backStack: NavBackStack<NavKey>
) {
    entry<LoginNavKey> {
        val viewModel = hiltViewModel<LoginViewModel>()
        LoginScreen(
            viewModel = viewModel,
            onRegisterClick = {
                backStack.clear()
                backStack.add(EmailVerifyNavKey)
            },
            onSuccess = {
                backStack.clear()
                backStack.add(HomeNavKey)
            }
        )
    }
}