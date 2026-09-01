package com.iftikar.outlier.core.models

data class Post(
    val id: Int,
    val title: String,
    val desc: String,
    val images: List<String>,
    val github: String,
    val liveLink: String,
    val techStack: List<String>,
    val tags: List<String>,
    val user: User
)
