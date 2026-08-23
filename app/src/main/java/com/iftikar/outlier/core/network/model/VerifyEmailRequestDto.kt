package com.iftikar.outlier.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class VerifyEmailRequestDto(
    val email: String,
    val otp: String
)
