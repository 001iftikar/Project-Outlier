package com.iftikar.outlier.core.data.repository

import com.iftikar.outlier.core.data.di.IoDispatcher
import com.iftikar.outlier.core.models.Session
import com.iftikar.outlier.core.domain.repository.AuthRepository
import com.iftikar.outlier.core.network.api.AuthApiService
import com.iftikar.outlier.core.network.model.LoginRequestDto
import com.iftikar.outlier.core.network.model.UserRequestDto
import com.iftikar.outlier.core.network.model.VerifyEmailRequestDto
import com.iftikar.outlier.core.result.AuthError
import com.iftikar.outlier.core.result.EmailVerificationError
import com.iftikar.outlier.core.result.EmptyResult
import com.iftikar.outlier.core.result.GenericError
import com.iftikar.outlier.core.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    @param:IoDispatcher private val io: CoroutineDispatcher
) : AuthRepository {
    override suspend fun checkIfUsernameExists(username: String): Result<Boolean, GenericError> = withContext(io) {
        try {
            val response = authApiService.checkUsernameExists(username)
            if (
                response.data == null
            ) {
                return@withContext Result.Error(GenericError.UNKNOWN)
            }
            Result.Success(response.data)
        } catch (ex: IOException) {
            Result.Error(GenericError.NO_INTERNET)
        } catch (ex: Exception) {
            ex.printStackTrace()
            Result.Error(GenericError.UNKNOWN)
        }
    }

    override suspend fun verifyOtp(email: String, code: String): Result<Session, EmailVerificationError> =
        withContext(
            Dispatchers.IO
        ) {
            try {
                val response = authApiService.verifyEmail(
                    VerifyEmailRequestDto(email, code)
                )
                if (response.data != null) {
                    val session = Session(
                        accessToken = response.data.accessToken,
                        refreshToken = response.data.refreshToken
                    )

                    Result.Success(session)
                } else {
                    val error = when(response.code) {
                        "VERIFICATION_NOT_FOUND" -> EmailVerificationError.VERIFICATION_NOT_FOUND
                        "OTP_ATTEMPTS_EXCEEDED" -> EmailVerificationError.OTP_ATTEMPTS_EXCEEDED
                        "OTP_EXPIRED" -> EmailVerificationError.OTP_EXPIRED
                        "INVALID_OTP" -> EmailVerificationError.INVALID_OTP
                        else -> {
                            EmailVerificationError.UNKNOWN
                        }
                    }
                    Result.Error(error)
                }
            } catch (ex: IOException) {
                Result.Error(EmailVerificationError.NO_INTERNET)
            } catch (ex: Exception) {
                ex.printStackTrace()
                Result.Error(EmailVerificationError.UNKNOWN)
            }
        }

    override suspend fun register(
        username: String,
        email: String,
        password: String,
        name: String,
        role: String
    ): Result<String, AuthError> = withContext(Dispatchers.IO) {
        try {
            val response = authApiService.registerUser(
                UserRequestDto(
                    username = username,
                    email = email,
                    password = password,
                    name = name,
                    role = role
                )
            )

            if (response.data != null) {
                Result.Success(response.data.email)
            } else {
                val error = when(response.code) {
                    "EMAIL_ALREADY_EXISTS" -> AuthError.USER_EXISTS
                    "VALIDATION_ERROR" -> AuthError.VALIDATION_ERROR
                    else -> AuthError.AUTH_FAILED
                }
                Result.Error(error)
            }
        } catch (e: IOException) {
            Result.Error(AuthError.NO_INTERNET)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(AuthError.UNKNOWN)
        }
    }

    override suspend fun login(
        username: String,
        password: String
    ): Result<Session, AuthError> = withContext(Dispatchers.IO) {
        try {
            val response = authApiService.login(LoginRequestDto(username, password))
            if (response.data != null) {
                val session = Session(response.data.accessToken, response.data.refreshToken)
                Result.Success(session)
            } else {
                val error = when(response.code) {
                    "PASSWORD_MISMATCH" -> AuthError.AUTH_FAILED
                    else -> AuthError.UNKNOWN
                }
                Result.Error(error)
            }
        } catch (ex: IOException) {
            Result.Error(AuthError.NO_INTERNET)
        } catch (ex: Exception) {
            ex.printStackTrace()
            Result.Error(AuthError.UNKNOWN)
        }
    }

    override suspend fun logout(): EmptyResult<AuthError> {
        TODO("Not yet implemented")
    }
}