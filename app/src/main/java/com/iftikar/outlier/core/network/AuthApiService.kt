package com.iftikar.outlier.core.network

import com.iftikar.outlier.core.network.model.AuthResponse
import com.iftikar.outlier.core.network.model.RegisterResponseDto
import com.iftikar.outlier.core.network.model.UserRequestDto
import com.iftikar.outlier.core.network.model.VerifyEmailRequestDto
import com.iftikar.outlier.core.util.sharedmodels.ApiResponse

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
}