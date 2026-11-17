package com.iftikar.outlier.core.network.impl

import com.iftikar.outlier.core.network.api.PostApiService
import com.iftikar.outlier.core.network.model.ApiResponse
import com.iftikar.outlier.core.network.model.CommentRequestDto
import com.iftikar.outlier.core.network.model.CommentResponseDto
import com.iftikar.outlier.core.network.model.CreatePostRequestDto
import com.iftikar.outlier.core.network.model.DoCommentResponseDto
import com.iftikar.outlier.core.network.model.PostResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import jakarta.inject.Inject
import jakarta.inject.Named
import kotlinx.serialization.json.Json
import java.io.File

class PostApiServiceImpl @Inject constructor(
    @Named("MainClient")
    private val httpClient: HttpClient
) : PostApiService {

    private val json = Json {
        encodeDefaults = true
    }

    override suspend fun createPost(
        request: CreatePostRequestDto,
        images: List<File>
    ): HttpResponse {

        return httpClient.post("posts") {

            setBody(
                MultiPartFormDataContent(
                    formData {

                        // Part 1: JSON post metadata.
                        append(
                            key = "request",
                            value = json.encodeToString(request),
                            headers = Headers.build {
                                append(
                                    HttpHeaders.ContentType,
                                    ContentType.Application.Json.toString()
                                )
                            }
                        )

                        // Part 2: Image files.
                        images.forEach { image ->

                            append(
                                key = "images",
                                value = image.readBytes(),
                                headers = Headers.build {

                                    append(
                                        HttpHeaders.ContentType,
                                        "image/webp"
                                    )

                                    append(
                                        HttpHeaders.ContentDisposition,
                                        "filename=\"${image.name}\""
                                    )
                                }
                            )
                        }
                    }
                )
            )
        }
    }

    override suspend fun getPosts(size: Int, cursor: String): ApiResponse<PostResponseDto> {
        return httpClient.get("posts") {
            parameter("size", size)
            parameter("cursor", cursor)
        }.body()
    }

    override suspend fun likePost(postId: String): HttpResponse {
        return httpClient.post("posts/$postId/like").body()
    }

    override suspend fun unlikePost(postId: String): HttpResponse {
        return httpClient.delete("posts/$postId/like").body()
    }

    override suspend fun getPostComments(postId: String, size: Int, cursor: String): ApiResponse<CommentResponseDto> {
        return httpClient.get("posts/$postId/comments") {
            parameter("size", size)
            parameter("cursor", cursor)
        }.body()
    }

    override suspend fun doComment(postId: String, requestDto: CommentRequestDto): ApiResponse<DoCommentResponseDto> {
        return httpClient.post("posts/$postId/comments") {
            contentType(ContentType.Application.Json)
            setBody(requestDto)
        }.body()
    }
}