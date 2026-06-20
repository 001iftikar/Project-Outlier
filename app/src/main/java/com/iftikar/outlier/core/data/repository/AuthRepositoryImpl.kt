package com.iftikar.outlier.core.data.repository

import com.iftikar.outlier.core.domain.repository.AuthRepository
import com.iftikar.outlier.core.result.AuthError
import com.iftikar.outlier.core.result.EmptyResult
import com.iftikar.outlier.core.result.Result
import com.iftikar.outlier.core.result.asEmptyDataResult
import io.appwrite.services.Account
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val account: Account
): AuthRepository {
    override suspend fun register(
        email: String,
        username: String,
        password: String
    ): EmptyResult<AuthError> {

    }
}