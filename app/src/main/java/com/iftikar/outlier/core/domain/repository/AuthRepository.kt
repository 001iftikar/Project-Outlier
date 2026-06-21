package com.iftikar.outlier.core.domain.repository

import com.iftikar.outlier.core.result.AuthError
import com.iftikar.outlier.core.result.EmptyResult
import com.iftikar.outlier.core.result.Result

interface AuthRepository {
    suspend fun register(email: String, password: String, username: String) : Result<String, AuthError>
    suspend fun login(email: String, password: String): Result<String, AuthError>
    suspend fun logout(): EmptyResult<AuthError>
}