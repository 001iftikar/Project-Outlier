package com.iftikar.outlier.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class CommentResponseDto(
    val comments: List<CommentDto>,
    val nextCursor: String?,
    val hasMore: Boolean,
    val commentsCount: Int
)

@Serializable
data class CommentAuthor(
    val id: String,
    val username: String,
    val name: String
)
