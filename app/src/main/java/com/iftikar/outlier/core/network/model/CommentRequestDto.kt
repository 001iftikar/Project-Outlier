package com.iftikar.outlier.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class CommentRequestDto(
    val content: String
)

@Serializable
data class DoCommentResponseDto(
    val comment: CommentDto,
    val commentsCount: Int
)