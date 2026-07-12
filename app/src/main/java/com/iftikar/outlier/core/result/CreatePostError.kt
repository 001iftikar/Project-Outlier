package com.iftikar.outlier.core.result

enum class CreatePostError : Error {
    UNAUTHORIZED, CONFLICT, TOO_MANY_REQUESTS, SERVER, TIMEOUT, NO_INTERNET, UNKNOWN
}