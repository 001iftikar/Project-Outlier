package com.iftikar.outlier.core.models

data class PostComments(
    val comments: List<Comment>,
    val cursor: String,
    val commentsCount: Int
)
