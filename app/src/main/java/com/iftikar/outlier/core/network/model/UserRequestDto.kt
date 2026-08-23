package com.iftikar.outlier.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class UserRequestDto(
    val username: String,
    val email: String,
    val password: String,
    val name: String,
    val role: String
)
