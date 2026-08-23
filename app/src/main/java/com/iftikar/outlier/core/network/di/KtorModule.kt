package com.iftikar.outlier.core.network.di

import com.iftikar.outlier.core.datastore.SessionManager
import com.iftikar.outlier.core.network.model.RefreshTokenRequest
import com.iftikar.outlier.core.util.sharedmodels.ApiResponse
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import jakarta.inject.Named
import jakarta.inject.Singleton
import kotlinx.serialization.json.Json

@Module
@InstallIn(SingletonComponent::class)
object KtorModule {
    @Singleton
    @Provides
    @Named("AuthClient")
    fun provideKtorAuthClient(): HttpClient {

        return HttpClient(OkHttp) {
            defaultRequest {
                url("https://project-outlier-backend.onrender.com/api/v1/")
            }
            expectSuccess = false

            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    }
                )
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 120000
                connectTimeoutMillis = 120000
                socketTimeoutMillis = 120000
            }

            install(Logging) {
                logger = Logger.ANDROID
                level = LogLevel.ALL
            }
        }
    }

    @Singleton
    @Provides
    @Named("MainClient")
    fun provideMainKtorClient(
        @Named("RefreshClient")
        refreshClient: HttpClient,
        sessionManager: SessionManager
    ): HttpClient {
        return HttpClient(OkHttp) {
            defaultRequest {
                url("https://project-outlier-backend.onrender.com/api/v1/")
            }
            expectSuccess = false
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; isLenient = true })
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        val accessToken = sessionManager.getAccessToken()
                        val refreshToken = sessionManager.getRefreshToken()
                        if (accessToken == null || refreshToken == null) {
                            null
                        } else {
                            BearerTokens(
                                accessToken = accessToken,
                                refreshToken = refreshToken
                            )
                        }
                    }

                    refreshTokens {

                        val refreshToken =
                            sessionManager.getRefreshToken()
                                ?: return@refreshTokens null

                        val response =
                            refreshClient.post("auth/refresh-token") {

                                contentType(ContentType.Application.Json)

                                setBody(
                                    RefreshTokenRequest(
                                        refreshToken = refreshToken
                                    )
                                )

                            }.body<ApiResponse<String>>()

                        val newAccessToken =
                            response.data
                                ?: return@refreshTokens null

                        sessionManager.saveAccessTokenWhenExpired(newAccessToken)

                        BearerTokens(
                            accessToken = newAccessToken,
                            refreshToken = refreshToken
                        )
                    }
                }
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 120000
                connectTimeoutMillis = 120000
            }
            install(Logging) {
                logger = Logger.ANDROID
                level = LogLevel.ALL
            }
        }
    }
}