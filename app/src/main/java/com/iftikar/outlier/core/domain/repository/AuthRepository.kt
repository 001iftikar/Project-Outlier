package com.iftikar.outlier.core.domain.repository

import com.iftikar.outlier.core.datastore.model.Session
import com.iftikar.outlier.core.result.AuthError
import com.iftikar.outlier.core.result.DescopeError
import com.iftikar.outlier.core.result.EmailVerificationError
import com.iftikar.outlier.core.result.EmptyResult
import com.iftikar.outlier.core.result.Result
import com.iftikar.outlier.core.result.GenericError

interface AuthRepository {
    suspend fun checkIfUsernameExists(username: String): Result<Boolean, GenericError>
    suspend fun verifyOtp(email: String, code: String): Result<Session, EmailVerificationError>
    suspend fun register(username: String, email: String, password: String, name: String, role: String) : Result<String, AuthError>

    suspend fun login(email: String, password: String): Result<Session, AuthError>
    suspend fun logout(): EmptyResult<AuthError>
}