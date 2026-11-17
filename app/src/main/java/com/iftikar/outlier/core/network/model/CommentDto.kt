package com.iftikar.outlier.core.network.model

import com.iftikar.outlier.core.designsystem.util.toTimeAgo
import com.iftikar.outlier.core.models.Comment
import kotlinx.serialization.Serializable

@Serializable
data class CommentDto(
    val id: String,
    val content: String,
    val author: CommentAuthor,
    val createdAt: String,
    val updatedAt: String
)

fun CommentDto.asComment() = Comment(
    id = id,
    authorName = author.name,
    content = content,
    profileImageUrl = null,
    updatedAt = updatedAt.toTimeAgo()
)