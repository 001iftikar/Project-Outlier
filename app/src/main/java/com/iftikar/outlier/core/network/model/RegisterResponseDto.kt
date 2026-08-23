package com.iftikar.outlier.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponseDto(
    val email: String
)
