package com.iftikar.outlier.core.models

/**
 * Carries the userId and expire to store in data store
 */
data class Session(
    val userId: String,
    val expire: String
)
