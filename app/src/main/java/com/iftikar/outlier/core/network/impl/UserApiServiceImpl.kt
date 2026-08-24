package com.iftikar.outlier.core.network.impl

import com.iftikar.outlier.core.network.api.UserApiService
import com.iftikar.outlier.core.network.model.ApiResponse
import com.iftikar.outlier.core.network.model.DrawerUserInfoDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import jakarta.inject.Inject
import jakarta.inject.Named

class UserApiServiceImpl @Inject constructor(
    @Named("MainClient")
    private val httpClient: HttpClient
) : UserApiService {
    override suspend fun getDrawerUserInfo(): ApiResponse<DrawerUserInfoDto> {
        return httpClient.get("users/drawer-user-info").body()
    }
}