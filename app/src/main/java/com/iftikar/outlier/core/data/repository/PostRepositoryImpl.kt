package com.iftikar.outlier.core.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.iftikar.outlier.core.data.di.IoDispatcher
import com.iftikar.outlier.core.data.util.toCompressedImageFile
import com.iftikar.outlier.core.domain.repository.PostRepository
import com.iftikar.outlier.core.models.Comment
import com.iftikar.outlier.core.models.HomePosts
import com.iftikar.outlier.core.models.PostComments
import com.iftikar.outlier.core.models.SendPost
import com.iftikar.outlier.core.network.api.PostApiService
import com.iftikar.outlier.core.network.api.UserApiService
import com.iftikar.outlier.core.network.model.ApiResponse
import com.iftikar.outlier.core.network.model.CommentRequestDto
import com.iftikar.outlier.core.network.model.CreatePostRequestDto
import com.iftikar.outlier.core.network.model.CreatePostResponseDto
import com.iftikar.outlier.core.network.model.asComment
import com.iftikar.outlier.core.network.model.asHomePosts
import com.iftikar.outlier.core.result.CreatePostError
import com.iftikar.outlier.core.result.EmptyResult
import com.iftikar.outlier.core.result.GenericError
import com.iftikar.outlier.core.result.GetPostError
import com.iftikar.outlier.core.result.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.IOException
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class PostRepositoryImpl @Inject constructor(
    @param:ApplicationContext
    private val context: Context,
    private val userApiService: UserApiService,
    private val postApiService: PostApiService,
    @param:IoDispatcher private val io: CoroutineDispatcher,
) : PostRepository {
    override suspend fun gitHubUrlCheck(url: String): Result<Boolean, GenericError> = withContext(
        Dispatchers.IO
    ) {
        try {
            val isValid =
                userApiService.isGitHubUrlValid(url).data ?: return@withContext Result.Error(
                    GenericError.UNKNOWN
                )
            Result.Success(isValid.valid)
        } catch (ex: IOException) {
            Result.Error(GenericError.NO_INTERNET)
        } catch (ex: Exception) {
            ex.printStackTrace()
            Result.Error(GenericError.UNKNOWN)
        }
    }

    override suspend fun createPost(
        sendPost: SendPost
    ): EmptyResult<CreatePostError> = withContext(io) {

        val compressedFiles = mutableListOf<File>()

        try {

            // 1. Validate the number of selected images.
            if (
                sendPost.images.isEmpty() ||
                sendPost.images.size > MAX_IMAGES
            ) {
                return@withContext Result.Error(
                    CreatePostError.INVALID_REQUEST
                )
            }

            // 2. Convert each selected URI into a compressed WebP file.
            for (imageString in sendPost.images) {

                val compressedFile = Uri.parse(imageString)
                    .toCompressedImageFile(context)

                if (compressedFile == null) {

                    return@withContext Result.Error(
                        CreatePostError.INVALID_REQUEST
                    )
                }

                compressedFiles.add(compressedFile)
            }

            // 3. Convert the UI model into the backend request DTO.
            val request = CreatePostRequestDto(
                title = sendPost.title.trim(),

                description = sendPost.description.trim(),

                githubUrl = sendPost.githubUrl
                    .trim()
                    .takeIf { it.isNotEmpty() },

                liveProjectUrl = sendPost.liveUrl
                    .trim()
                    .takeIf { it.isNotEmpty() },

                techStack = sendPost.techStack
                    .split(',')
                    .map { it.trim() }
                    .filter { it.isNotEmpty() },

                tags = sendPost.tags
                    .split(',')
                    .map { it.trim().removePrefix("#").trim() }
                    .filter { it.isNotEmpty() }
            )

            // 4. Send metadata and image files together.
            val response = postApiService.createPost(
                request = request,
                images = compressedFiles
            )
            // 5. Handle successful responses.
            if (response.status.value in 200..299) {

                val body =
                    response.body<ApiResponse<CreatePostResponseDto>>()

                if (body.data == null) {
                    Result.Error(CreatePostError.UNKNOWN)
                } else {
                    Result.Success(Unit)
                }

            } else {

                // 6. Deserialize the backend error envelope.
                val body = runCatching {
                    response.body<ApiResponse<CreatePostResponseDto>>()
                }.getOrNull()

                // 7. Convert HTTP status and backend error code
                //    into our domain error.
                val error = when (response.status.value) {

                    400 -> CreatePostError.INVALID_REQUEST

                    401 -> CreatePostError.UNAUTHORIZED

                    403 -> CreatePostError.FORBIDDEN

                    409 -> CreatePostError.CONFLICT

                    413 -> CreatePostError.PAYLOAD_TOO_LARGE

                    422 -> when (body?.code) {

                        "UNSAFE_URL" ->
                            CreatePostError.UNSAFE_URL

                        "GITHUB_REPOSITORY_NOT_FOUND",
                        "GITHUB_REPOSITORY_MOVED" ->
                            CreatePostError.INVALID_GITHUB_URL

                        else ->
                            CreatePostError.INVALID_REQUEST
                    }

                    429 -> CreatePostError.TOO_MANY_REQUESTS

                    502 -> CreatePostError.IMAGE_UPLOAD_FAILED

                    503 -> CreatePostError.SERVICE_UNAVAILABLE

                    504 -> CreatePostError.TIMEOUT

                    in 500..599 -> CreatePostError.SERVER

                    else -> CreatePostError.UNKNOWN
                }

                Log.e(
                    TAG,
                    "Create post failed. HTTP ${response.status.value}, " +
                            "code=${body?.code}, message=${body?.message}"
                )

                Result.Error(error)
            }

        } catch (exception: HttpRequestTimeoutException) {

            Result.Error(CreatePostError.TIMEOUT)

        } catch (exception: CancellationException) {

            throw exception

        } catch (exception: IOException) {

            Result.Error(CreatePostError.NO_INTERNET)

        } catch (exception: Exception) {

            Log.e(TAG, "Post creation failed.", exception)

            Result.Error(CreatePostError.UNKNOWN)

        } finally {

            // 8. Delete all temporary compressed files.
            compressedFiles.forEach { file ->

                if (file.exists() && !file.delete()) {

                    Log.w(
                        TAG,
                        "Could not delete temporary image file: ${file.name}"
                    )
                }
            }
        }
    }

    override suspend fun getPosts(size: Int, cursor: String): Result<HomePosts, GetPostError> =
        withContext(io) {
            try {
                val response = postApiService.getPosts(size, cursor)
                if (response.data == null) {
                    when (response.code) {
                        "INVALID_PAGE_SIZE" -> {
                            Log.e("Feed-Repo", "getPosts: ${response.message}")
                            return@withContext Result.Error(GetPostError.DEVELOPER_MESSED_UP)
                        }

                        "INVALID_CURSOR" -> {
                            Log.e("Feed-Repo", "getPosts: ${response.message}")
                            return@withContext Result.Error(GetPostError.SERVER)
                        }

                        "INTERNAL_SERVER_ERROR" -> {
                            Log.e("Feed-Repo", "getPosts: ${response.message}")
                            return@withContext Result.Error(GetPostError.SERVER)
                        }
                    }
                }

                val data = response.data ?: return@withContext Result.Error(GetPostError.UNKNOWN)
                Result.Success(data.asHomePosts())
            } catch (ex: IOException) {
                Result.Error(GetPostError.NO_INTERNET)
            } catch (ex: Exception) {
                ex.printStackTrace()
                Result.Error(GetPostError.UNKNOWN)
            }
        }

    override suspend fun likePost(postId: String): EmptyResult<GenericError> = withContext(io) {
        try {
            val response = postApiService.likePost(postId)
            if (response.status.value in 200..299) {
                Result.Success(Unit)
            } else {
                Result.Error(GenericError.UNKNOWN)
            }
        } catch (ex: IOException) {
            Result.Error(GenericError.NO_INTERNET)
        } catch (ex: Exception) {
            Log.e(TAG, "likePost: Failed to like post", ex)
            Result.Error(GenericError.UNKNOWN)
        }
    }

    override suspend fun unlikePost(postId: String): EmptyResult<GenericError> = withContext(io) {
        try {
            val response = postApiService.unlikePost(postId)
            if (response.status.value in 200..299) {
                Result.Success(Unit)
            } else {
                Result.Error(GenericError.UNKNOWN)
            }
        } catch (ex: Exception) {
            Log.e(TAG, "likePost: Failed to unlike post", ex)
            Result.Error(GenericError.UNKNOWN)
        }
    }

    override suspend fun getComments(postId: String, size: Int, cursor: String): Result<PostComments, GenericError> = withContext(io) {
        try {
            val response = postApiService.getPostComments(
                postId = postId,
                size = size,
                cursor = cursor
            ).data ?: return@withContext Result.Error(GenericError.UNKNOWN)
            val postComments = PostComments(
                comments = response.comments.map { it.asComment() },
                cursor = response.nextCursor ?: "",
                commentsCount = response.commentsCount
            )
            Result.Success(postComments)
        } catch (ex: Exception) {
            Log.e("Comments-Repo", "getComments: Failed to get comments", ex)
            Result.Error(GenericError.UNKNOWN)
        }
    }

    override suspend fun doComment(
        postId: String,
        content: String
    ): Result<Pair<Comment, Int>, GenericError> = withContext(io) {
        try {
            val response = postApiService.doComment(postId, CommentRequestDto(content))
            val data = response.data ?: return@withContext Result.Error(GenericError.UNKNOWN)
            Result.Success(Pair(data.comment.asComment(), data.commentsCount))
        } catch (ex: IOException) {
            Result.Error(GenericError.NO_INTERNET)
        } catch (ex: Exception) {
            Log.e("Comments-Repo", "doComment: Failed to do comment", ex)
            Result.Error(GenericError.UNKNOWN)
        }
    }

    private companion object {
        const val TAG = "PostRepository"
        const val MAX_IMAGES = 3
    }
}




















