package com.iftikar.outlier.core.models

data class Comment(
    val id: String,
    val authorName: String,
    val content: String,
    val profileImageUrl: String? = null,
    val updatedAt: String
)