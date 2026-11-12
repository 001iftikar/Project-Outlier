package com.iftikar.outlier.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class CreatePostResponseDto(
    val postId: String,
    val thumbnailUrl: String
)