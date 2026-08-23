package com.iftikar.outlier.core.result

enum class EmailVerificationError : Error {
    VERIFICATION_NOT_FOUND,
    OTP_ATTEMPTS_EXCEEDED,
    OTP_EXPIRED,
    INVALID_OTP,
    UNKNOWN,
    NO_INTERNET
}