package com.iftikar.outlier.core.appwrite.model

import com.iftikar.outlier.core.models.Post

data class PostDto(
    val userId: String,
    val title: String,
    val description: String,
    val images: List<String>,
    val github: String,
    val liveLink: String,
    val techStack: List<String>,
    val tags: List<String>
)

fun PostDto.asExternalModel(): Post {
    return Post(
        userId = userId,
        title = title,
        description = description,
        images = images,
        techStack = techStack.joinToString(),
        githubUrl = github,
        liveUrl = liveLink,
        tags = tags.joinToString(separator = ", ", prefix = "#")
    )
}

















