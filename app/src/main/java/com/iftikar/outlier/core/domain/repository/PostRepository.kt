package com.iftikar.outlier.core.domain.repository

import com.iftikar.outlier.core.models.Post
import com.iftikar.outlier.core.result.CreatePostError
import com.iftikar.outlier.core.result.EmptyResult
import com.iftikar.outlier.core.result.GetPostError
import com.iftikar.outlier.core.result.Result

interface PostRepository {
    suspend fun createPost(post: Post, userId: String): EmptyResult<CreatePostError>
    suspend fun getPosts(): Result<List<Post>, GetPostError>
}