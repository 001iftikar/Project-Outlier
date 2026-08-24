package com.iftikar.outlier.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val code: String,
    val message: String,
    val data: T?
)