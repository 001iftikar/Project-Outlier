package com.iftikar.outlier.core.network.model

import com.iftikar.outlier.core.models.HomePosts
import com.iftikar.outlier.core.models.Post
import com.iftikar.outlier.core.models.PostAuthor
import kotlinx.serialization.Serializable

@Serializable
data class PostResponseDto(
    val hasMore: Boolean,
    val nextCursor: String?,
    val posts: List<PostResponse>
)

@Serializable
data class PostResponse(
    val commentsCount: Int,
    val createdAt: String,
    val description: String,
    val githubUrl: String?,
    val id: String,
    val images: List<String>,
    val likedByCurrentUser: Boolean,
    val likesCount: Int,
    val liveProjectUrl: String?,
    val tags: List<String>,
    val techStack: List<String>,
    val thumbnailUrl: String,
    val title: String,
    val user: PostAuthorResponse
)

fun PostResponse.asPost(): Post {
    return Post(
        id = id,
        title = title,
        desc = description,
        images = images,
        github = githubUrl,
        liveLink = liveProjectUrl,
        techStack = techStack,
        tags = tags,
        postAuthor = user.asPostAuthor(),
        likesCount = likesCount,
        commentsCount = commentsCount,
        likedByCurrentUser = likedByCurrentUser
    )
}

@Serializable
data class PostAuthorResponse(
    val id: String,
    val name: String,
    val techStack: List<String>,
    val username: String
)
fun PostAuthorResponse.asPostAuthor(): PostAuthor {
    return PostAuthor(
        id = id,
        name = name,
        techStack = techStack
    )
}

fun PostResponseDto.asHomePosts(): HomePosts {
    return HomePosts(
        posts = this.posts.map { it.asPost() },
        cursors = setOf(this.nextCursor)
    )
}




























