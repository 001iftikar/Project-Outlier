package com.iftikar.outlier.core.appwrite.model

import com.iftikar.outlier.core.models.Post

data class PostDto(
    val userId: String,
    val userName: String,
    val title: String,
    val description: String,
    val images: List<String>,
    val github: String?,
    val liveLink: String?,
    val techStack: List<String>,
    val tags: List<String>
)

fun PostDto.asExternalModel(imageUrls: List<String>): Post {
    return Post(
        userId = userId,
        userName = userName,
        title = title,
        description = description,
        images = imageUrls,
        techStack = techStack.joinToString(),
        githubUrl = github ?: "",
        liveUrl = liveLink ?: "",
        tags = tags.joinToString(separator = ", ", prefix = "#")
    )
}

















