package com.iftikar.outlier.core.result

enum class AuthError : Error {
    USER_EXISTS,
    INVALID_EMAIL,
    AUTH_FAILED,
    PASSWORD_MISMATCH,
    TOO_MANY_REQUESTS,
    REQUEST_TIMEOUT,
    NO_INTERNET,
    UNKNOWN
}