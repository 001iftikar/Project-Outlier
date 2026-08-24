package com.iftikar.outlier.feature.auth.impl

import android.util.Patterns

data class LoginScreenState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isPasswordVisible: Boolean = false
) {
    val isRegisterButtonEnabled: Boolean
        get() = username.isNotEmpty() &&
                password.isNotEmpty() &&
                !isLoading
    val buttonText: String
        get() = when {
            isLoading -> "Signing In"
            error != null -> "Try again"
            else -> "Log In"
        }
}

sealed interface LoginScreenAction {
    data class OnUsernameChange(val username: String) : LoginScreenAction
    data class OnPasswordChange(val password: String) : LoginScreenAction
    data object OnPasswordEyeClick : LoginScreenAction
    data object OnLoginClick : LoginScreenAction
}

sealed interface LoginScreenEvent {
    data object OnSuccess : LoginScreenEvent
    data class OnError(val error: String) : LoginScreenEvent
}