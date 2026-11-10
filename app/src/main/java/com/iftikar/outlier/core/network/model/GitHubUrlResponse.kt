package com.iftikar.outlier.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class GitHubUrlResponse(
    val status: String,
    val valid: Boolean,
    val normalizedUrl: String
)
