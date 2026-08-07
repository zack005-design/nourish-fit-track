package com.fitnessapp.ai

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * CameraX capture utility for Gemini Nano AI image analysis.
 *
 * Usage:
 * 1. Call [bindPreview] to attach camera to a [PreviewView].
 * 2. Call [captureImageBitmap] to get a [Bitmap] from the current frame.
 * 3. Pass the [Bitmap] to [GeminiNanoEngine.buildImagePrompt] for AI analysis.
 */
class CameraCapture(private val context: Context) {

    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private var imageCapture: ImageCapture? = null

    /**
     * Binds the camera lifecycle to the provided [PreviewView].
     * Should be called from a composable or fragment after permission is granted.
     */
    fun bindPreview(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        lensFacing: Int = CameraSelector.LENS_FACING_BACK,
        onBound: () -> Unit = {}
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
                onBound()
            } catch (e: Exception) {
                Log.e(TAG, "Camera bind failed", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    /**
     * Captures one frame and returns a [Bitmap].
     * Suspends the caller until the frame is ready.
     * @throws IllegalStateException if the camera is not bound.
     */
    suspend fun captureImageBitmap(): Bitmap = withContext(Dispatchers.IO) {
        val capture = imageCapture ?: error("Camera not bound. Call bindPreview() first.")

        suspendCancellableCoroutine { continuation ->
            capture.takePicture(
                cameraExecutor,
                object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy) {
                        try {
                            val bitmap = image.toBitmap()
                            image.close()
                            continuation.resume(bitmap)
                        } catch (e: Exception) {
                            image.close()
                            continuation.resumeWithException(e)
                        }
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.e(TAG, "Image capture failed", exception)
                        continuation.resumeWithException(exception)
                    }
                }
            )
        }
    }

    /**
     * Converts the captured [Bitmap] to a flat float array suitable for model input.
     * Normalizes pixel RGB values to [0.0, 1.0] range.
     * Output shape: [1, 224, 224, 3] flattened as a FloatArray.
     */
    fun bitmapToInputTensor(bitmap: Bitmap, targetSize: Int = 224): FloatArray {
        val scaled = Bitmap.createScaledBitmap(bitmap, targetSize, targetSize, true)
        val tensor = FloatArray(targetSize * targetSize * 3)
        var idx = 0
        for (y in 0 until targetSize) {
            for (x in 0 until targetSize) {
                val pixel = scaled.getPixel(x, y)
                tensor[idx++] = ((pixel shr 16) and 0xFF) / 255f  // R
                tensor[idx++] = ((pixel shr 8) and 0xFF) / 255f   // G
                tensor[idx++] = (pixel and 0xFF) / 255f            // B
            }
        }
        Log.d(TAG, "Tensor ready: [1, $targetSize, $targetSize, 3] (${tensor.size} floats)")
        if (scaled != bitmap && !scaled.isRecycled) {
            scaled.recycle()
        }
        return tensor
    }

    fun shutdown() {
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraCapture"
    }
}
