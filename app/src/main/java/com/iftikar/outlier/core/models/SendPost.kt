package com.iftikar.outlier.core.models

data class SendPost(
    val userId: String,
    val title: String,
    val description: String,
    val images: List<String>,
    val techStack: String,
    val githubUrl: String,
    val liveUrl: String,
    val tags: String
)
