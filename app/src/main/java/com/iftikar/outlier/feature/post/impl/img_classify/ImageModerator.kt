package com.iftikar.outlier.feature.post.impl.img_classify

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import androidx.core.graphics.scale
import com.iftikar.outlier.R
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.FloatBuffer

@Singleton
class ImageModerator @Inject constructor(
    @ApplicationContext context: Context
) {
    private val appContext = context.applicationContext
    private val environment = OrtEnvironment.getEnvironment()

    private val sessionDelegate = lazy {
        val modelBytes = appContext.resources
            .openRawResource(R.raw.image_safety_classifier_s)
            .use { it.readBytes() }

        environment.createSession(modelBytes)
    }

    private val session: OrtSession by sessionDelegate

    suspend fun classify(uri: Uri): ImageModerationPrediction {
        val bitmap = withContext(Dispatchers.IO) {
            decodeImage(uri)
        }

        return try {
            withContext(Dispatchers.Default) {
                runInference(bitmap)
            }
        } finally {
            bitmap.recycle()
        }
    }

    private fun decodeImage(uri: Uri): Bitmap {
        val bounds = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        appContext.contentResolver.openInputStream(uri).use { stream ->
            requireNotNull(stream) { "Cannot open image: $uri" }
                .let {
                    BitmapFactory.decodeStream(it, null, bounds)
                }
        }

        require(bounds.outWidth > 0 && bounds.outHeight > 0) {
            "Invalid image"
        }

        // Decode a smaller version first to reduce memory usage.
        val divisor = (
                minOf(bounds.outWidth, bounds.outHeight) / IMAGE_SIZE
                ).coerceAtLeast(1)

        val sampleSize = Integer.highestOneBit(divisor)

        val options = BitmapFactory.Options().apply {
            inSampleSize = sampleSize.coerceAtLeast(1)
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }

        val decoded = appContext.contentResolver
            .openInputStream(uri)
            .use { stream ->
                requireNotNull(stream) { "Cannot open image: $uri" }
                BitmapFactory.decodeStream(stream, null, options)
            } ?: error("Unable to decode image")

        val resized = decoded.scale(IMAGE_SIZE, IMAGE_SIZE)

        if (resized !== decoded) {
            decoded.recycle()
        }

        return resized
    }

    private fun runInference(
        bitmap: Bitmap
    ): ImageModerationPrediction {
        val planeSize = IMAGE_SIZE * IMAGE_SIZE
        val pixels = IntArray(planeSize)

        bitmap.getPixels(
            pixels,
            0,
            IMAGE_SIZE,
            0,
            0,
            IMAGE_SIZE,
            IMAGE_SIZE
        )

        // ONNX input layout: [batch, channels, height, width].
        // Store all red pixels, then green, then blue.
        val input = FloatArray(3 * planeSize)
        /*
        Why do we need a FloatArray?
        The model expects floating-point numbers, not Android's packed ARGB integers.
        It also expects three separate color channels.
         */

        for (i in pixels.indices) {
            val pixel = pixels[i] // Gets the packed integer for a single pixel

            input[i] = Color.red(pixel).toFloat() // Extracts its red component, converts the value to a Float, and stores it in the red block
            input[planeSize + i] = Color.green(pixel).toFloat() // Same as above
            input[2 * planeSize + i] = Color.blue(pixel).toFloat() // Do I need to tell?
        } // This loop is essentially converting Android's pixel representation into the model's tensor representation

        val tensor = OnnxTensor.createTensor(
            environment,
            FloatBuffer.wrap(input),
            longArrayOf(1, 3, IMAGE_SIZE.toLong(), IMAGE_SIZE.toLong())
        )

        tensor.use {
            session.run(mapOf("image" to tensor)).use { output -> // Creates a map connecting the model's input name, "image", to the tensor we created. That input name must match the ONNX model's declared input name; the model card uses "image"
                // This is where the neural network performs inference
                @Suppress("UNCHECKED_CAST")
                val batch = output[0].value as Array<FloatArray> // The model returns an output tensor. output[0] retrieves the first output, and .value retrieves its underlying Java representation

                val probabilities = batch[0] // We supplied exactly one image, so the batch size is one. Therefore, we select the first row to obtain the scores for our image

                check(probabilities.size == 3) {
                    "Unexpected number of model outputs"
                }

                // Exact output order from the model card.
                val scores = mapOf(
                    SafetyLabel.NSFL to probabilities[0],
                    SafetyLabel.NSFW to probabilities[1],
                    SafetyLabel.SFW to probabilities[2]
                )

                val highest = scores.maxBy { it.value } // examines the entries in the map and returns the entry with the largest value

                return ImageModerationPrediction(
                    label = highest.key,
                    confidence = highest.value,
                    scores = scores
                )
            }
        }
        // BONUS EXPLANATION if you didn't know when to use "use{}"
        /*
        Why two .use {} calls?
        ONNX Runtime tensors and inference results own native resources. These shouldn't be left open indefinitely

        The outer use closes the input tensor. The inner use closes the inference result and its associated output values

        Kotlin's use is a resource-management utility that performs cleanup even if an exception is thrown within the block

        Without these cleanup calls, repeated image classification could retain native memory longer than necessary
         */
    }

    companion object {
        private const val IMAGE_SIZE = 224
    }
}