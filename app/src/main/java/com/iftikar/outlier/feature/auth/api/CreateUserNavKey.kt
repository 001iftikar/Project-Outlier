package com.iftikar.outlier.feature.auth.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class CreateUserNavKey(
    val username: String,
    val email: String,
    val role: String
) : NavKey
