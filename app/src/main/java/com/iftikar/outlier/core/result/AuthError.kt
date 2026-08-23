package com.iftikar.outlier.core.result

enum class AuthError : Error {
    USER_EXISTS,
    VALIDATION_ERROR,
    AUTH_FAILED,
    PASSWORD_MISMATCH,
    TOO_MANY_REQUESTS,
    REQUEST_TIMEOUT,
    NO_INTERNET,
    UNKNOWN
}