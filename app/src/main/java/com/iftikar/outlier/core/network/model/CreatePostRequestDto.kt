package com.iftikar.outlier.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class CreatePostRequestDto(
    val title: String,
    val description: String,
    val githubUrl: String?,
    val liveProjectUrl: String?,
    val techStack: List<String>,
    val tags: List<String>
)