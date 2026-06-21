package com.iftikar.outlier.core.data.repository

import com.iftikar.outlier.DATABASE_ID
import com.iftikar.outlier.core.domain.repository.UserProfileRepository
import com.iftikar.outlier.core.result.AuthError
import com.iftikar.outlier.core.result.CreateUserError
import com.iftikar.outlier.core.result.EmptyResult
import com.iftikar.outlier.core.result.Result
import io.appwrite.Permission
import io.appwrite.Role
import io.appwrite.exceptions.AppwriteException
import io.appwrite.services.Account
import io.appwrite.services.TablesDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.IOException
import javax.inject.Inject
import kotlin.collections.mapOf

class UserProfileRepositoryImpl @Inject constructor(
    private val account: Account,
    private val tablesDB: TablesDB
) : UserProfileRepository {
    override suspend fun createUser(
        username: String,
        email: String,
        role: String
    ): EmptyResult<CreateUserError> = withContext(Dispatchers.IO) {
        try {
            val userId = account.get().id
            tablesDB.createRow(
                databaseId = DATABASE_ID,
                tableId = "users",
                rowId = userId,
                data = mapOf(
                    "username" to username,
                    "email" to email,
                    "role" to role
                ),
                permissions = listOf(
                    Permission.read(Role.any()),
                    Permission.update(Role.user(userId))
                )
            )
            return@withContext Result.Success(Unit)
        } catch (ex: AppwriteException) {
            ex.printStackTrace()
            val error = when(ex.code){
                401 -> CreateUserError.NOT_AUTHORIZED
                else -> CreateUserError.UNKNOWN
            }
            return@withContext Result.Error(error)
        } catch (e: IOException) {
            return@withContext Result.Error(CreateUserError.NO_INTERNET)
        }
        catch (e: Exception) {
            return@withContext Result.Error(CreateUserError.UNKNOWN)
        }
    }
}








