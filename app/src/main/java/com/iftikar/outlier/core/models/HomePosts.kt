package com.iftikar.outlier.core.models

data class HomePosts(
    val posts: List<Post> = emptyList(),
    val cursors: Set<String?> = emptySet()
)
