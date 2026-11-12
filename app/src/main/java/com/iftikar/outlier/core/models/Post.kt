package com.iftikar.outlier.core.models

data class Post(
    val id: String,
    val title: String,
    val desc: String,
    val images: List<String>,
    val github: String?,
    val liveLink: String?,
    val techStack: List<String>,
    val tags: List<String>,
    val postAuthor: PostAuthor,
    val likesCount: Int,
    val commentsCount: Int,
    val likedByCurrentUser: Boolean
)
