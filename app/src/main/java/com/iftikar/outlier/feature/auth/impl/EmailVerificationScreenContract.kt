package com.iftikar.outlier.feature.auth.impl

import android.util.Patterns

data class EmailVerificationScreenState(
    val showEmailScreen: Boolean = true,
    val email: String = "",
    val code: String = "",
    val isSendingOtp: Boolean = false,
    val isVerifyingOtp: Boolean = false,
    val maskedEmail: String? = null,
    val sendOtpError: String? = null,
    val verifyOtpError: String? = null
) {
    val isEmailValid: Boolean
        get() = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val emailError: String?
        get() = if (email.isNotEmpty() && !isEmailValid) "Please enter a valid email" else null
}

sealed interface EmailVerificationScreenAction {
    data class OnEmailChange(val email: String) : EmailVerificationScreenAction
    data class OnCodeChange(val code: String) : EmailVerificationScreenAction
    data object OnChangeEmailClick : EmailVerificationScreenAction
    data object OnSendOtpClick : EmailVerificationScreenAction
    data object OnVerifyOtpClick : EmailVerificationScreenAction
}
sealed interface EmailVerificationScreenEvent {
    data class OnSuccess(val email: String) : EmailVerificationScreenEvent
}