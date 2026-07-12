package com.iftikar.outlier.core.domain.repository

import android.net.Uri

interface StorageRepository {
    suspend fun uploadFiles(uris: List<Uri>): List<String>
}