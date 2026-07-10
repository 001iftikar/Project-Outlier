package com.iftikar.outlier.core.domain.repository

import com.iftikar.outlier.core.result.CreateUserError
import com.iftikar.outlier.core.result.EmptyResult

interface UserProfileRepository {
    suspend fun createUser(name: String, email: String, role: String): EmptyResult<CreateUserError>
}