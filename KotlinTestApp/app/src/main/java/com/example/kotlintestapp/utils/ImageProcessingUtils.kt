package com.example.kotlintestapp.utils

import android.graphics.Bitmap
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import org.opencv.core.Core
import com.example.kotlintestapp.data.ImageSize

object ImageProcessingUtils {
    // ✅ Convert Bitmap to OpenCV Mat
    fun bitmapToMat(bitmap: Bitmap, mat: Mat) {
        val width = bitmap.width
        val height = bitmap.height
        val intArray = IntArray(width * height)
        bitmap.getPixels(intArray, 0, width, 0, 0, width, height)
        val byteArray = ByteArray(width * height * 4)
        for (i in intArray.indices) {
            val pixel = intArray[i]
            byteArray[i * 4] = ((pixel shr 16) and 0xFF).toByte() // R
            byteArray[i * 4 + 1] = ((pixel shr 8) and 0xFF).toByte() // G
            byteArray[i * 4 + 2] = (pixel and 0xFF).toByte() // B
            byteArray[i * 4 + 3] = ((pixel shr 24) and 0xFF).toByte() // A
        }
        mat.put(0, 0, byteArray)
    }

    // ✅ Convert OpenCV Mat to Bitmap
    fun matToBitmap(mat: Mat, bitmap: Bitmap) {
        val width = mat.cols()
        val height = mat.rows()

        val channels = mat.channels()
        val byteArray = ByteArray(width * height * channels)
        mat.get(0, 0, byteArray)

        val intArray = IntArray(width * height)

        if (channels == 1) {  // Grayscale image case
            for (i in intArray.indices) {
                val gray = byteArray[i].toInt() and 0xFF
                intArray[i] = (255 shl 24) or (gray shl 16) or (gray shl 8) or gray
            }
        } else if (channels == 3 || channels == 4) {  // RGB or RGBA image
            for (i in intArray.indices) {
                val r = byteArray[i * channels].toInt() and 0xFF
                val g = byteArray[i * channels + 1].toInt() and 0xFF
                val b = byteArray[i * channels + 2].toInt() and 0xFF
                val a = if (channels == 4) byteArray[i * channels + 3].toInt() and 0xFF else 255

                intArray[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
            }
        } else {
            throw IllegalArgumentException("Unsupported Mat channels: $channels")
        }

        bitmap.setPixels(intArray, 0, width, 0, 0, width, height)
    }

    // ✅ Resize Bitmap based on `ImageSize`
    fun resizeBitmap(bitmap: Bitmap, targetSize: ImageSize): Bitmap {
        val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
        val (targetWidth, targetHeight) = if (aspectRatio > 1) {
            Pair(targetSize.width, (targetSize.width / aspectRatio).toInt())
        } else {
            Pair((targetSize.height * aspectRatio).toInt(), targetSize.height)
        }

        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
    }

    // ✅ Remove Background (Simple HSV-based approach)
    fun removeBackground(srcBitmap: Bitmap): Bitmap {
        val srcMat = Mat(srcBitmap.height, srcBitmap.width, CvType.CV_8UC4)
        bitmapToMat(srcBitmap, srcMat)

        // Convert to HSV
        val hsvMat = Mat()
        Imgproc.cvtColor(srcMat, hsvMat, Imgproc.COLOR_RGB2HSV)

        // Define color range (Assume the top-left pixel is background)
        val bgColor = hsvMat.get(0, 0)
        val hue = bgColor[0]
        val lowerHue = (hue - 30).coerceAtLeast(0.0)
        val upperHue = (hue + 30).coerceAtMost(179.0)

        // Create a mask for background
        val mask = Mat()
        Core.inRange(hsvMat, Scalar(lowerHue, 50.0, 50.0), Scalar(upperHue, 255.0, 255.0), mask)

        // Convert original to RGBA (Add alpha channel)
        val resultMat = Mat()
        Imgproc.cvtColor(srcMat, resultMat, Imgproc.COLOR_RGB2RGBA)

        // Apply transparency
        for (i in 0 until mask.rows()) {
            for (j in 0 until mask.cols()) {
                if (mask.get(i, j)[0] == 255.0) { // Background detected
                    val rgba = resultMat.get(i, j)
                    rgba[3] = 0.0 // Set alpha to transparent
                    resultMat.put(i, j, *rgba)
                }
            }
        }

        // Convert Mat back to Bitmap
        val transparentBitmap = Bitmap.createBitmap(srcBitmap.width, srcBitmap.height, Bitmap.Config.ARGB_8888)
        matToBitmap(resultMat, transparentBitmap)

        return transparentBitmap
    }
}
