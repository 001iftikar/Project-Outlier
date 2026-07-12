package com.iftikar.outlier.core.models

/**
 * Carries the userId, expire, user's name and role to store in data store
 */
data class Session(
    val userId: String,
    val expire: String,
    val userName: String,
    val role: String
)
