package com.iftikar.outlier.core.appwrite.util

import com.iftikar.outlier.APPWRITE_PROJECT_ID
import com.iftikar.outlier.APPWRITE_PUBLIC_ENDPOINT
import com.iftikar.outlier.POST_BUCKET_ID

fun getImageUrl(fileId: String): String {
    return "$APPWRITE_PUBLIC_ENDPOINT/storage/buckets/$POST_BUCKET_ID/files/$fileId/view?project=$APPWRITE_PROJECT_ID"
}