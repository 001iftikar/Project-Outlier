package com.iftikar.outlier.core.data.repository

import android.content.Context
import android.net.Uri
import com.iftikar.outlier.POST_BUCKET_ID
import com.iftikar.outlier.core.data.di.DefaultDispatcher
import com.iftikar.outlier.core.data.di.IoDispatcher
import com.iftikar.outlier.core.data.util.toCompressedImageFile
import com.iftikar.outlier.core.domain.repository.StorageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import io.appwrite.ID
import io.appwrite.models.InputFile
import io.appwrite.services.Storage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AppwriteStorageImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val storage: Storage,
    @param:IoDispatcher private val io: CoroutineDispatcher,
    @param:DefaultDispatcher private val default: CoroutineDispatcher,
) : StorageRepository {
    override suspend fun uploadFiles(uris: List<Uri>): List<String> = withContext(io) {
        val deferredUploads = uris.map { uri ->
            async {
                val inputFile = withContext(default) {
                    val file = uri.toCompressedImageFile(context)
                        ?: throw Exception("File Compression failed")
                    InputFile.fromFile(file)
                }
                storage.createFile(
                    bucketId = POST_BUCKET_ID,
                    fileId = ID.unique(),
                    file = inputFile
                ).id
            }
        }
        deferredUploads.awaitAll()
    }
}