package com.iftikar.outlier.core.data.model

import com.iftikar.outlier.core.models.SendPost

data class PostRequestDto(
    val userId: String,
    val title: String,
    val description: String,
    val images: List<String>,
    val github: String?,
    val liveLink: String?,
    val techStack: List<String>,
    val tags: List<String>
)

fun PostRequestDto.asExternalModel(imageUrls: List<String>): SendPost {
    return SendPost(
        userId = userId,
        title = title,
        description = description,
        images = imageUrls,
        techStack = techStack.joinToString(),
        githubUrl = github ?: "",
        liveUrl = liveLink ?: "",
        tags = tags.joinToString(separator = ", ", prefix = "#")
    )
}

















