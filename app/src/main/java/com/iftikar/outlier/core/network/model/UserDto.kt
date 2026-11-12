package com.iftikar.outlier.core.network.model

import kotlinx.serialization.SerialName

data class UserDto(
    @SerialName("\$id") val id: String?,
    val name: String,
    val email: String,
    val role: String
)
