package com.iftikar.outlier.core.appwrite.model

import com.iftikar.outlier.core.models.User

data class UserDto(
    val name: String,
    val email: String,
    val role: String
)

fun UserDto.asExternalModel(): User {
    return User(
        name = name,
        email = email,
        role = role
    )
}
