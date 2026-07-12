package com.iftikar.outlier.core.data.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

fun Uri.toCompressedImageFile(context: Context, quality: Int = 80): File? {
    val contentResolver = context.contentResolver

    // 1. Decode the massive original image from the Uri into memory
    val inputStream = contentResolver.openInputStream(this)
    val originalBitmap = BitmapFactory.decodeStream(inputStream)
    inputStream?.close()

    if (originalBitmap == null) return null

    // 2. Create the temporary file for the compressed version
    val tempFileName = "outlier_compressed_${UUID.randomUUID()}.webp"
    val tempFile = File(context.cacheDir, tempFileName)

    return try {
        val outputStream = FileOutputStream(tempFile)

        // 3. Crush the image using WebP (Highly efficient, excellent quality)
        // Note: For API 30+, use CompressFormat.WEBP_LOSSY. For older, use WEBP.
        originalBitmap.compress(Bitmap.CompressFormat.WEBP, quality, outputStream)

        outputStream.flush()
        outputStream.close()

        // 4. Free up the RAM immediately
        originalBitmap.recycle()

        // Return the tiny, Appwrite-ready file!
        tempFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}