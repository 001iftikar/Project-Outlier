package com.iftikar.outlier.core.domain.repository

import com.iftikar.outlier.core.models.DrawerUserInfo
import com.iftikar.outlier.core.result.GenericError
import com.iftikar.outlier.core.result.Result

interface UserProfileRepository {
    suspend fun getDrawerUserInfo(): Result<DrawerUserInfo, GenericError>
}