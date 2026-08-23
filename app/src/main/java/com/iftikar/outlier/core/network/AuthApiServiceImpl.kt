package com.iftikar.outlier.core.network

import com.iftikar.outlier.core.network.model.AuthResponse
import com.iftikar.outlier.core.network.model.RegisterResponseDto
import com.iftikar.outlier.core.network.model.UserRequestDto
import com.iftikar.outlier.core.network.model.VerifyEmailRequestDto
import com.iftikar.outlier.core.util.sharedmodels.ApiResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import jakarta.inject.Inject
import jakarta.inject.Named

class AuthApiServiceImpl @Inject constructor(
    @Named("AuthClient")
    private val httpClient: HttpClient
) : AuthApiService {
    override suspend fun checkUsernameExists(
        username: String
    ): ApiResponse<Boolean> {
        return httpClient.get("auth/check-username") {
            url {
                appendPathSegments(username)
            }
        }
            .body()
    }

    override suspend fun registerUser(userRequestDto: UserRequestDto): ApiResponse<RegisterResponseDto> {
        return httpClient.post("auth/register") {
            contentType(ContentType.Application.Json)
            setBody(userRequestDto)
        }.body<ApiResponse<RegisterResponseDto>>()
    }

    override suspend fun verifyEmail(verifyEmailRequestDto: VerifyEmailRequestDto): ApiResponse<AuthResponse> {
        return httpClient.post("auth/verify-email") {
            contentType(ContentType.Application.Json)
            setBody(verifyEmailRequestDto)
        }.body<ApiResponse<AuthResponse>>()
    }
}










