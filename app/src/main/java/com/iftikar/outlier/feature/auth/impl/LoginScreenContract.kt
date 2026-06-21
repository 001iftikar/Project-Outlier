package com.iftikar.outlier.feature.auth.impl

import android.util.Patterns

data class LoginScreenState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isPasswordVisible: Boolean = false
) {
    private val isEmailValid: Boolean
        get() = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private val isPasswordValid: Boolean
        get() = password.isNotEmpty()

    val emailError: String?
        get() = if (email.isNotEmpty() && !isEmailValid) "Please enter a valid email" else null

    val isRegisterButtonEnabled: Boolean
        get() = isEmailValid &&
                isPasswordValid &&
                !isLoading
    val buttonText: String
        get() = when {
            isLoading -> "Signing In"
            error != null -> "Try again"
            else -> "Log In"
        }
}

sealed interface LoginScreenAction {
    data class OnEmailChange(val email: String) : LoginScreenAction
    data class OnPasswordChange(val password: String) : LoginScreenAction
    data object OnPasswordEyeClick : LoginScreenAction
    data object OnLoginClick : LoginScreenAction
}

sealed interface LoginScreenEvent {
    data object OnSuccess : LoginScreenEvent
    data class OnError(val error: String) : LoginScreenEvent
}