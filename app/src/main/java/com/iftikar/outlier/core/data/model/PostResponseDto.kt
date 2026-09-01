package com.iftikar.outlier.core.data.model

import com.iftikar.outlier.core.models.Post
import kotlinx.serialization.SerialName

data class PostResponseDto(
    @SerialName("\$id") val id: String?,
    val title: String,
    val description: String,
    val images: List<String>?,
    val github: String?,
    val liveLink: String?,
    val techStack: List<String>?,
    val tags: List<String>?,
    val user: UserDto
)
//
//fun PostResponseDto.asExternalModule(images: List<String>): Post {
//    return Post(
//        id = id ?: "",
//        title = title,
//        desc = description,
//        images = images,
//        github = github ?: "",
//        liveLink = liveLink ?: "",
//        techStack = techStack ?: emptyList(),
//        tags = tags?.joinToString(separator = " ") { "#$it" } ?: "",
//        user = user.asExternalModel(images)
//    )
//}
