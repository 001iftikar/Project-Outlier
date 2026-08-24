package com.iftikar.outlier.core.data.repository

import com.iftikar.outlier.core.data.di.IoDispatcher
import com.iftikar.outlier.core.domain.repository.UserProfileRepository
import com.iftikar.outlier.core.models.DrawerUserInfo
import com.iftikar.outlier.core.network.api.UserApiService
import com.iftikar.outlier.core.network.model.esExternalModel
import com.iftikar.outlier.core.result.GenericError
import com.iftikar.outlier.core.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okio.IOException
import javax.inject.Inject

class UserProfileRepositoryImpl @Inject constructor(
    private val userApiService: UserApiService,
    @param:IoDispatcher private val io: CoroutineDispatcher
) : UserProfileRepository {
    override suspend fun getDrawerUserInfo(): Result<DrawerUserInfo, GenericError> = withContext(io) {
        try {
            val response = userApiService.getDrawerUserInfo()
            if (response.data == null) {
                Result.Error(GenericError.UNKNOWN)
            } else {
                Result.Success(response.data.esExternalModel())
            }
        } catch (ex: IOException) {
            Result.Error(GenericError.NO_INTERNET)
        } catch (ex: Exception) {
            Result.Error(GenericError.UNKNOWN)
        }
    }
}








