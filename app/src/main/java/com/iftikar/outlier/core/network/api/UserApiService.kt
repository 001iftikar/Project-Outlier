package com.iftikar.outlier.core.network.api

import com.iftikar.outlier.core.network.model.ApiResponse
import com.iftikar.outlier.core.network.model.DrawerUserInfoDto

interface UserApiService {
    suspend fun getDrawerUserInfo(): ApiResponse<DrawerUserInfoDto>
}