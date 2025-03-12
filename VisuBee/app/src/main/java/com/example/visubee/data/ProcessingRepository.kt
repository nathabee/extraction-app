package com.example.visubee.data

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import com.example.visubee.utils.ImageProcessingUtils.resizeBitmap
import com.example.visubee.utils.ImageProcessingUtils.removeBackground
import com.example.visubee.utils.ImageProcessingUtils.matToBitmap
import com.example.visubee.utils.ImageProcessingUtils.bitmapToMat

class ProcessingRepository {

    suspend fun processImage(
        inputBitmap: Bitmap?,
        backgroundBitmap: Bitmap?,
        threshold1: Float,
        threshold2: Float,
        tolerance: Int,
        brightness: Int,
        selectedSize: ImageSize
    ): Pair<Bitmap?, Bitmap?> = withContext(Dispatchers.IO) {
        if (inputBitmap == null) return@withContext Pair(null, null)

        // ✅ Step 1: Resize BEFORE processing for efficiency (maxFactor = 10)
        val resizedBitmap = adaptiveResizeBitmap(inputBitmap, selectedSize, maxFactor = 10)

        // ✅ Step 2: Edge Detection
        val imgMat = Mat(resizedBitmap.height, resizedBitmap.width, CvType.CV_8UC4)
        bitmapToMat(resizedBitmap, imgMat)

        Imgproc.cvtColor(imgMat, imgMat, Imgproc.COLOR_RGBA2GRAY)
        Imgproc.Canny(imgMat, imgMat, threshold1.toDouble(), threshold2.toDouble())

        val edgeBitmap = Bitmap.createBitmap(resizedBitmap.width, resizedBitmap.height, Bitmap.Config.ARGB_8888)
        Imgproc.cvtColor(imgMat, imgMat, Imgproc.COLOR_GRAY2RGBA)
        matToBitmap(imgMat, edgeBitmap)

        // ✅ Step 3: Background Removal on the resized image
        val transparentBitmap = if (backgroundBitmap != null) {
            removeBackground(resizedBitmap, backgroundBitmap, tolerance)
        } else {
            removeBackground(resizedBitmap, null, tolerance)
        }

        // ✅ Step 4: Resize AGAIN to ensure exact output size
        val finalTransparentBitmap = resizeBitmap(transparentBitmap, selectedSize)

        return@withContext Pair(edgeBitmap, finalTransparentBitmap)
    }


    private fun adaptiveResizeBitmap(bitmap: Bitmap, targetSize: ImageSize, maxFactor: Int = 10): Bitmap {
        val targetWidth = targetSize.width
        val targetHeight = targetSize.height

        // Calculate scale factor
        val scaleFactor = maxFactor.coerceAtMost(
            (bitmap.width / targetWidth).coerceAtMost(bitmap.height / targetHeight)
        )

        // Resize only if the image is `N` times larger
        return if (scaleFactor > 1) {
            val newWidth = bitmap.width / scaleFactor
            val newHeight = bitmap.height / scaleFactor
            Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        } else {
            bitmap // Keep original size if already within threshold
        }
    }



    /*fun saveBitmapToGallery(context: Context, bitmap: Bitmap, fileName: String, selectedSize: ImageSize) {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$fileName-${selectedSize.name}.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/KotlinTestApp")
        }

        val resolver = context.contentResolver
        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        imageUri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
        }
    }*/


}
