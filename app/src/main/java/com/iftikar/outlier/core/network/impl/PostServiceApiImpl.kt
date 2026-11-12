package com.iftikar.outlier.core.network.impl

import com.iftikar.outlier.core.network.api.PostApiService
import com.iftikar.outlier.core.network.model.CreatePostRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
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
}