package com.iftikar.outlier.core.domain.repository

import com.iftikar.outlier.core.models.Comment
import com.iftikar.outlier.core.models.HomePosts
import com.iftikar.outlier.core.models.PostComments
import com.iftikar.outlier.core.models.SendPost
import com.iftikar.outlier.core.result.CreatePostError
import com.iftikar.outlier.core.result.EmptyResult
import com.iftikar.outlier.core.result.GenericError
import com.iftikar.outlier.core.result.GetPostError
import com.iftikar.outlier.core.result.Result

interface PostRepository {
    suspend fun gitHubUrlCheck(url: String): Result<Boolean, GenericError>
    suspend fun createPost(sendPost: SendPost): EmptyResult<CreatePostError>
    suspend fun getPosts(size: Int, cursor: String = ""): Result<HomePosts, GetPostError>
    suspend fun likePost(postId: String): EmptyResult<GenericError>
    suspend fun unlikePost(postId: String): EmptyResult<GenericError>
    suspend fun getComments(
        postId: String,
        size: Int,
        cursor: String
    ): Result<PostComments, GenericError>
    suspend fun doComment(postId: String, content: String): Result<Pair<Comment, Int>, GenericError>
}