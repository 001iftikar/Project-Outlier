package com.iftikar.outlier.core.data.model

import com.iftikar.outlier.core.models.User
import kotlinx.serialization.SerialName

data class UserDto(
    @SerialName("\$id") val id: String?,
    val name: String,
    val email: String,
    val role: String
)

fun UserDto.asExternalModel(images: List<String>): User {
    return User(
        id = id ?: "",
        name = name,
        email = email,
        role = role
    )
}
