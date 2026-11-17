package com.iftikar.outlier.core.models

data class HomePosts(
    val posts: List<Post>,
    val cursor: String?
)
