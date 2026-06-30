package com.iftikar.outlier.feature.auth.impl

data class RegisterScreenState(
    val name: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isDevChecked: Boolean = false,
    val isRecruiterChecked: Boolean = false,
    val isPasswordVisible: Boolean = false
) {
    private val isPasswordValid: Boolean
        get() = password.length >= 8

    private val isRoleSelected: Boolean
        get() = isDevChecked || isRecruiterChecked

    val passwordError: String?
        get() = if (password.isNotEmpty() && !isPasswordValid) "Password must be at least 8 characters" else null

    val isRoleRequiredText: String
        get() = if (!isRoleSelected) "(required)" else ""
    val isRegisterButtonEnabled: Boolean
        get() =
            isPasswordValid &&
                    name.length >= 3 &&
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
    data class OnNameChange(val username: String) : RegisterScreenAction
    data class OnPasswordChange(val password: String) : RegisterScreenAction
    data class OnIsDevChecked(val checked: Boolean) : RegisterScreenAction
    data class OnIsRecruiterChecked(val checked: Boolean) : RegisterScreenAction
    data object OnPasswordEyeClick : RegisterScreenAction
    data object OnRegisterClick : RegisterScreenAction
}

sealed interface RegisterScreenEvent {
    data class OnSuccess(val username: String, val email: String, val role: Role) :
        RegisterScreenEvent

    data class OnError(val error: String) : RegisterScreenEvent
}

enum class Role {
    DEVELOPER, RECRUITER
}
