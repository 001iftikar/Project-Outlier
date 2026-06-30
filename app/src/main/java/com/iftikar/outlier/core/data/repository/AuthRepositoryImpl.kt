package com.iftikar.outlier.core.data.repository

import com.descope.Descope
import com.descope.session.DescopeSession
import com.descope.types.DeliveryMethod
import com.descope.types.DescopeException
import com.iftikar.outlier.DATABASE_ID
import com.iftikar.outlier.core.domain.repository.AuthRepository
import com.iftikar.outlier.core.models.Session
import com.iftikar.outlier.core.result.AuthError
import com.iftikar.outlier.core.result.DescopeError
import com.iftikar.outlier.core.result.EmptyResult
import com.iftikar.outlier.core.result.Result
import dagger.Lazy
import io.appwrite.ID
import io.appwrite.Query
import io.appwrite.exceptions.AppwriteException
import io.appwrite.services.Account
import io.appwrite.services.TablesDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.IOException
import java.net.UnknownHostException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val account: Lazy<Account>,
    private val tablesDB: TablesDB
) : AuthRepository {
    override suspend fun sendOtp(email: String): Result<String, DescopeError> =
        withContext(Dispatchers.IO) {
            try {
                val maskedEmail = Descope.otp.signUp(method = DeliveryMethod.Email, loginId = email)
                Result.Success(maskedEmail)
            } catch (ex: DescopeException) {
                if (ex.cause is UnknownHostException) {
                    Result.Error(DescopeError.NO_INTERNET)
                } else {
                    Result.Error(DescopeError.UNKNOWN)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                Result.Error(DescopeError.UNKNOWN)
            }
        }

    override suspend fun verifyOtp(email: String, code: String): Result<String, DescopeError> =
        withContext(
            Dispatchers.IO
        ) {
            try {
                val authResponse =
                    Descope.otp.verify(method = DeliveryMethod.Email, loginId = email, code = code)
                val session = DescopeSession(authResponse)
                Descope.sessionManager.manageSession(session)
                Result.Success(email)
            } catch (ex: DescopeException) {
                if (ex.cause is UnknownHostException) {
                    Result.Error(DescopeError.NO_INTERNET)
                } else {
                    when (ex) {
                        DescopeException.wrongOtpCode -> Result.Error(DescopeError.WRONG_OTP)
                        else -> {
                            ex.printStackTrace()
                            Result.Error(DescopeError.UNKNOWN)
                        }
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                Result.Error(DescopeError.UNKNOWN)
            }
        }

    override suspend fun register(
        email: String,
        password: String
    ): Result<Session, AuthError> = withContext(Dispatchers.IO) {
        try {
            val userExists = checkUserExists(email = email)
            if (userExists) return@withContext Result.Error(AuthError.USER_EXISTS)
            account.get().create(
                userId = ID.unique(),
                email = email,
                password = password
            )
            val session = account.get().createEmailPasswordSession(
                email = email,
                password = password
            )
            return@withContext Result.Success(
                Session(
                    userId = session.userId,
                    expire = session.expire
                )
            )
        } catch (e: AppwriteException) {
            val authError = when (e.code) {
                409 -> AuthError.USER_EXISTS
                429 -> AuthError.TOO_MANY_REQUESTS
                401 -> AuthError.AUTH_FAILED
                408, 504 -> AuthError.REQUEST_TIMEOUT
                400 -> {
                    // 400 is a general "Bad Request". We can check the message to be more specific.
                    val msg = e.message?.lowercase() ?: ""
                    when {
                        msg.contains("email") -> AuthError.INVALID_EMAIL
                        msg.contains("password") -> AuthError.PASSWORD_MISMATCH
                        else -> AuthError.AUTH_FAILED
                    }
                }

                else -> AuthError.UNKNOWN
            }
            return@withContext Result.Error(authError)

        } catch (e: IOException) {
            return@withContext Result.Error(AuthError.NO_INTERNET)
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext Result.Error(AuthError.UNKNOWN)
        }
    }

    override suspend fun login(
        email: String,
        password: String
    ): Result<Session, AuthError> = withContext(Dispatchers.IO) {
        try {
            val session =
                account.get().createEmailPasswordSession(email = email, password = password)
            Result.Success(Session(userId = session.userId, expire = session.expire))
        } catch (e: AppwriteException) {
            val authError = when (e.code) {
                409 -> AuthError.USER_EXISTS
                429 -> AuthError.TOO_MANY_REQUESTS
                401 -> AuthError.AUTH_FAILED
                408, 504 -> AuthError.REQUEST_TIMEOUT
                400 -> {
                    // 400 is a general "Bad Request". We can check the message to be more specific.
                    val msg = e.message?.lowercase() ?: ""
                    when {
                        msg.contains("email") -> AuthError.INVALID_EMAIL
                        msg.contains("password") -> AuthError.PASSWORD_MISMATCH
                        else -> AuthError.AUTH_FAILED
                    }
                }

                else -> AuthError.UNKNOWN
            }
            return@withContext Result.Error(authError)
        } catch (ex: Exception) {
            ex.printStackTrace()
            Result.Error(AuthError.UNKNOWN)
        }

    }

    override suspend fun logout(): EmptyResult<AuthError> = withContext(Dispatchers.IO) {
        try {
            account.get().deleteSession("current")
            Result.Success(Unit)
        } catch (ex: IOException) {
            Result.Error(AuthError.NO_INTERNET)
        } catch (ex: Exception) {
            ex.printStackTrace()
            Result.Error(AuthError.UNKNOWN)
        }
    }

    private suspend fun checkUserExists(email: String): Boolean = withContext(
        Dispatchers.IO
    ) {
        try {
            val response = tablesDB.listRows(
                databaseId = DATABASE_ID,
                tableId = "users",
                queries = listOf(
                    Query.equal("email", email),
                    Query.limit(1)
                )
            )
            return@withContext response.total != 0L
        } catch (ex: Exception) {
            ex.printStackTrace()
            false
        }
    }
}