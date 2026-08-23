package com.iftikar.outlier.feature.auth.impl

import android.util.Patterns

data class RegisterScreenState(
    val name: String = "",
    val username: String = "",
    val checkingUsername: Boolean? = null,
    val isUsernameAvailable: Boolean? = null,
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isDevChecked: Boolean = false,
    val isRecruiterChecked: Boolean = false,
    val isPasswordVisible: Boolean = false
) {
    val usernameAvailability: String?
        get() {
            return if (checkingUsername != null && !checkingUsername) {
                if (isUsernameAvailable != null) {
                    if (isUsernameAvailable) "Username is available" else "Username is not available"
                } else {
                    null
                }
            } else {
                null
            }
        }
    private val isEmailValid: Boolean
        get() = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val emailError: String?
        get() = if (email.isNotEmpty() && !isEmailValid) "Please enter a valid email" else null
    private val isPasswordValid: Boolean
        get() = password.length >= 6

    private val isRoleSelected: Boolean
        get() = isDevChecked || isRecruiterChecked
    val passwordError: String?
        get() = if (password.isNotEmpty() && !isPasswordValid) "Password must be at least 6 characters" else null

    val isRoleRequiredText: String
        get() = if (!isRoleSelected) "(required)" else ""
    val isRegisterButtonEnabled: Boolean
        get() = isPasswordValid && isEmailValid && name.length >= 3 && isRoleSelected && !isLoading && isUsernameAvailable == true && username.isNotEmpty()
    val buttonText: String
        get() = when {
            isLoading -> "Sending OTP"
            error != null -> "Try again"
            else -> "Register"
        }

    val role: Role
        get() = if (isDevChecked) Role.DEVELOPER else Role.RECRUITER
}

sealed interface RegisterScreenAction {
    data class OnNameChange(val name: String) : RegisterScreenAction
    data class OnUsernameChange(val username: String) : RegisterScreenAction
    data class OnEmailChange(val email: String) : RegisterScreenAction
    data class OnPasswordChange(val password: String) : RegisterScreenAction
    data class OnIsDevChecked(val checked: Boolean) : RegisterScreenAction
    data class OnIsRecruiterChecked(val checked: Boolean) : RegisterScreenAction
    data object OnPasswordEyeClick : RegisterScreenAction
    data object OnRegisterClick : RegisterScreenAction
}

sealed interface RegisterScreenEvent {
    data class OnSuccess(val email: String) : RegisterScreenEvent

    data class OnError(val error: String) : RegisterScreenEvent
}

enum class Role {
    DEVELOPER, RECRUITER
}
