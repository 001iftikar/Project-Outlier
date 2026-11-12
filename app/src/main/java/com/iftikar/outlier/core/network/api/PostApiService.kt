package com.iftikar.outlier.core.network.api

import com.iftikar.outlier.core.network.model.CreatePostRequestDto
import io.ktor.client.statement.HttpResponse
import java.io.File

interface PostApiService {

    suspend fun createPost(
        request: CreatePostRequestDto,
        images: List<File>
    ): HttpResponse
}