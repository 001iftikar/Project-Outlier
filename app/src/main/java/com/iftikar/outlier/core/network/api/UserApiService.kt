package com.iftikar.outlier.core.network.api

import com.iftikar.outlier.core.network.model.ApiResponse
import com.iftikar.outlier.core.network.model.DrawerUserInfoDto
import com.iftikar.outlier.core.network.model.GitHubUrlResponse

interface UserApiService {
    suspend fun getDrawerUserInfo(): ApiResponse<DrawerUserInfoDto>
    suspend fun isGitHubUrlValid(url: String): ApiResponse<GitHubUrlResponse>
}