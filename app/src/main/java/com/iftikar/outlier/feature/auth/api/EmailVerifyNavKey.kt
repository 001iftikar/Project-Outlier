package com.iftikar.outlier.feature.auth.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class EmailVerifyNavKey(val email: String) : NavKey