package com.iftikar.outlier.core.network.api

import com.iftikar.outlier.core.network.model.ApiResponse
import com.iftikar.outlier.core.network.model.CommentRequestDto
import com.iftikar.outlier.core.network.model.CommentResponseDto
import com.iftikar.outlier.core.network.model.CreatePostRequestDto
import com.iftikar.outlier.core.network.model.DoCommentResponseDto
import com.iftikar.outlier.core.network.model.PostResponseDto
import com.iftikar.outlier.core.network.model.SearchResponseDto
import io.ktor.client.statement.HttpResponse
import java.io.File

interface PostApiService {

    suspend fun createPost(
        request: CreatePostRequestDto,
        images: List<File>
    ): HttpResponse

    suspend fun getPosts(
        size: Int = 10,
        cursor: String = ""
    ): ApiResponse<PostResponseDto>

    suspend fun likePost(postId: String): HttpResponse
    suspend fun unlikePost(postId: String): HttpResponse
    suspend fun getPostComments(postId: String, size: Int = 20, cursor: String): ApiResponse<CommentResponseDto>
    suspend fun doComment(postId: String, requestDto: CommentRequestDto): ApiResponse<DoCommentResponseDto>
    suspend fun searchPosts(query: String, size: Int, cursor: String, matchedTechStackInsteadCursor: String): ApiResponse<SearchResponseDto>
}