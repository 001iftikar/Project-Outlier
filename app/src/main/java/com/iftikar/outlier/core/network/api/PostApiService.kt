package com.iftikar.outlier.core.network.api

import com.iftikar.outlier.core.network.model.ApiResponse
import com.iftikar.outlier.core.network.model.CreatePostRequestDto
import com.iftikar.outlier.core.network.model.PostResponseDto
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
}