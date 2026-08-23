package com.iftikar.outlier.feature.auth.impl

data class EmailVerificationScreenState(
    val code: String = "",
    val isVerifyingOtp: Boolean = false,
    val verifyOtpError: String? = null
)

sealed interface EmailVerificationScreenAction {
    data class OnCodeChange(val code: String) : EmailVerificationScreenAction
    data object OnVerifyOtpClick : EmailVerificationScreenAction
}
sealed interface EmailVerificationScreenEvent {
    data object OnSuccess : EmailVerificationScreenEvent
    data class OnError(val error: String) : EmailVerificationScreenEvent
}