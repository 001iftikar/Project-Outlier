package com.iftikar.outlier.feature.auth.impl

import android.util.Patterns

data class RegisterScreenState(
    val email: String = "",
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isDevChecked: Boolean = false,
    val isRecruiterChecked: Boolean = false,
    val isPasswordVisible: Boolean = false
) {

    private val isEmailValid: Boolean
        get() = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private val isPasswordValid: Boolean
        get() = password.length >= 8

    private val isRoleSelected: Boolean
        get() = isDevChecked || isRecruiterChecked

    private val isUsernameValid: Boolean
        get() = !username.contains(" ") && username.isNotEmpty()
    val emailError: String?
        get() = if (email.isNotEmpty() && !isEmailValid) "Please enter a valid email" else null

    val passwordError: String?
        get() = if (password.isNotEmpty() && !isPasswordValid) "Password must be at least 8 characters" else null
    val usernameError: String?
        get() = if (username.isNotEmpty() && !isUsernameValid) "Username cannot contain spaces" else null

    val isRoleRequiredText: String
        get() = if (!isRoleSelected) "(required)" else ""
    val isRegisterButtonEnabled: Boolean
        get() = isEmailValid &&
                isPasswordValid &&
                isUsernameValid &&
                isRoleSelected &&
                !isLoading
    val buttonText: String
        get() = when {
            isLoading -> "Registering"
            error != null -> "Try again"
            else -> "Register"
        }

    val role: Role
        get() = if (isDevChecked) Role.DEVELOPER else Role.RECRUITER
}

sealed interface RegisterScreenAction {
    data class OnEmailChange(val email: String) : RegisterScreenAction
    data class OnUsernameChange(val username: String) : RegisterScreenAction
    data class OnPasswordChange(val password: String) : RegisterScreenAction
    data class OnIsDevChecked(val checked: Boolean) : RegisterScreenAction
    data class OnIsRecruiterChecked(val checked: Boolean) : RegisterScreenAction
    data object OnPasswordEyeClick : RegisterScreenAction
    data object OnRegisterClick : RegisterScreenAction
}

sealed interface RegisterScreenEvent {
    data class OnSuccess(val username: String, val email: String, val role: Role) : RegisterScreenEvent
    data class OnError(val error: String) : RegisterScreenEvent
}

enum class Role {
    DEVELOPER, RECRUITER
}
