package com.iftikar.outlier.core.domain.repository

import com.iftikar.outlier.core.result.AuthError
import com.iftikar.outlier.core.result.EmptyResult
import com.iftikar.outlier.core.result.onError
import com.iftikar.outlier.core.result.onSuccess
import javax.inject.Inject

interface AuthRepository {
    suspend fun register(email: String, username: String, password: String) : EmptyResult<AuthError>
}