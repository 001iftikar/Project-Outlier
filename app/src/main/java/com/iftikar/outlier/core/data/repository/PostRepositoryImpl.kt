package com.iftikar.outlier.core.data.repository

import android.util.Log
import com.iftikar.outlier.DATABASE_ID
import com.iftikar.outlier.POSTS_ID
import com.iftikar.outlier.core.appwrite.util.getImageUrl
import com.iftikar.outlier.core.data.di.IoDispatcher
import com.iftikar.outlier.core.data.model.PostResponseDto
import com.iftikar.outlier.core.data.model.asExternalModule
import com.iftikar.outlier.core.domain.repository.PostRepository
import com.iftikar.outlier.core.models.Post
import com.iftikar.outlier.core.models.SendPost
import com.iftikar.outlier.core.result.CreatePostError
import com.iftikar.outlier.core.result.EmptyResult
import com.iftikar.outlier.core.result.GetPostError
import com.iftikar.outlier.core.result.Result
import io.appwrite.ID
import io.appwrite.Permission
import io.appwrite.Query
import io.appwrite.Role
import io.appwrite.exceptions.AppwriteException
import io.appwrite.services.TablesDB
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okio.IOException
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val tablesDB: TablesDB,
    @param:IoDispatcher private val io: CoroutineDispatcher,
) : PostRepository {
    override suspend fun createPost(sendPost: SendPost, userId: String): EmptyResult<CreatePostError> = withContext(io) {
        try {
            val data = mapOf(
                "user" to sendPost.userId,
                "title" to sendPost.title,
                "description" to sendPost.description,
                "images" to sendPost.images,
                "github" to sendPost.githubUrl,
                "liveLink" to sendPost.liveUrl,
                "techStack" to sendPost.techStack.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                "tags" to sendPost.tags.split(",").map { it.trim().removePrefix("#") }.filter { it.isNotEmpty() }
            )
            tablesDB.createRow(
                databaseId = DATABASE_ID,
                tableId = POSTS_ID,
                rowId = ID.unique(),
                data = data,
                permissions = listOf(
                    Permission.delete(Role.user(userId)),
                    Permission.update(Role.user(userId))
                )
            )
            Result.Success(Unit)
        } catch (ex: IOException) {
            Result.Error(CreatePostError.NO_INTERNET)
        } catch (ex: AppwriteException) {
            ex.printStackTrace()
            val error = when(ex.code) {
                401 -> CreatePostError.UNAUTHORIZED
                409 -> CreatePostError.CONFLICT
                429 -> CreatePostError.TOO_MANY_REQUESTS
                500 -> CreatePostError.SERVER
                504 -> CreatePostError.TIMEOUT
                else -> CreatePostError.UNKNOWN
            }
            Result.Error(error)
        }
        catch (ex: Exception) {
            ex.printStackTrace()
            Result.Error(CreatePostError.UNKNOWN)
        }
    }

    override suspend fun getPosts(): Result<List<Post>, GetPostError> = withContext(io) {
        try {
            val postRows = tablesDB.listRows(
                databaseId = DATABASE_ID,
                tableId = POSTS_ID,
                queries = listOf(
                    // 1. Order them newest to oldest
                    Query.orderDesc("\$createdAt"),

                    // 2. TELL Appwrite: "Give me the post data (*),
                    // AND fully expand the user object (user.*) so I can see who posted it!"
                    Query.select(listOf("*", "user.*"))
                ),
                nestedType = PostResponseDto::class.java
            ).rows
            val posts = postRows.map { row ->
                val imageUrls = row.data.images?.map {
                    getImageUrl(it)
                }
                row.data.asExternalModule(imageUrls ?: emptyList())
            }
            Log.d("GetPost", "getPosts: $posts")
            Result.Success(posts)
        } catch (ex: IOException) {
            Result.Error(GetPostError.NO_INTERNET)
        } catch (ex: AppwriteException) {
            ex.printStackTrace()
            Log.e("GetPost", "getPosts: ${ex.localizedMessage}", )
            val error = when(ex.code) {
                404 -> GetPostError.NO_DATA
                500 -> GetPostError.SERVER
                else -> GetPostError.UNKNOWN
            }
            Result.Error(error)
        }
        catch (ex: Exception) {
            ex.printStackTrace()
            Result.Error(GetPostError.UNKNOWN)
        }
    }
}




















