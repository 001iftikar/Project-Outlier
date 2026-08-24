package com.iftikar.outlier.core.network.api

import com.iftikar.outlier.core.network.model.ApiResponse
import com.iftikar.outlier.core.network.model.AuthResponse
import com.iftikar.outlier.core.network.model.LoginRequestDto
import com.iftikar.outlier.core.network.model.RegisterResponseDto
import com.iftikar.outlier.core.network.model.UserRequestDto
import com.iftikar.outlier.core.network.model.VerifyEmailRequestDto

interface AuthApiService {
    /**
     * Check for username if it's available while user's typing
     */
    suspend fun checkUsernameExists(username: String): ApiResponse<Boolean>

    /**
     * Input user details, send an otp to the email
     */
    suspend fun registerUser(userRequestDto: UserRequestDto): ApiResponse<RegisterResponseDto>

    /**
     * verify the email, if success, create the user in db
     */
    suspend fun verifyEmail(verifyEmailRequestDto: VerifyEmailRequestDto): ApiResponse<AuthResponse>
    suspend fun login(requestDto: LoginRequestDto): ApiResponse<AuthResponse>
}