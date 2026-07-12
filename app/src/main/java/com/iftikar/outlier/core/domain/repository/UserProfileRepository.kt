package com.iftikar.outlier.core.domain.repository

import com.iftikar.outlier.core.result.EmptyResult
import com.iftikar.outlier.core.result.UserError

interface UserProfileRepository {
    suspend fun createUser(name: String, email: String, role: String): EmptyResult<UserError>
}