package com.iftikar.outlier.core.domain.repository

import com.iftikar.outlier.core.models.Post
import com.iftikar.outlier.core.result.CreatePostError
import com.iftikar.outlier.core.result.EmptyResult

interface PostRepository {
    suspend fun createPost(post: Post, userId: String): EmptyResult<CreatePostError>
}