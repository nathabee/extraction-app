package com.example.visubee.utils

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import com.example.visubee.data.ImageSize

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



    // ✅ Remove Background (Improved with Tolerance & Brightness)
    // ✅ Remove Background Based on Reference Image or Top-Left 10% Region
    fun removeBackground(
        inputBitmap: Bitmap,
        referenceBitmap: Bitmap? = null,
        tolerance: Int = 30
    ): Bitmap {
        val srcMat = Mat()
        Utils.bitmapToMat(inputBitmap, srcMat)

        // Convert to HSV
        val hsvMat = Mat()
        Imgproc.cvtColor(srcMat, hsvMat, Imgproc.COLOR_RGB2HSV)

        // Determine background color
        val bgColor = if (referenceBitmap != null) {
            getDominantColor(referenceBitmap, tolerance) // ✅ Use reference image
        } else {
            getDominantColorFromTopLeft(inputBitmap, tolerance) // ✅ Use top-left 10% region
        }

        val lowerHue = (bgColor[0] - tolerance).coerceAtLeast(0.0)
        val upperHue = (bgColor[0] + tolerance).coerceAtMost(179.0)

        val lowerColor = Scalar(lowerHue, 50.0, 50.0)
        val upperColor = Scalar(upperHue, 255.0, 255.0)

        // Create mask to detect background
        val mask = Mat()
        Core.inRange(hsvMat, lowerColor, upperColor, mask)

        // Convert image to RGBA (add alpha channel)
        val resultMat = Mat()
        Imgproc.cvtColor(srcMat, resultMat, Imgproc.COLOR_RGB2RGBA)

        // Apply transparency based on mask
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
        val transparentBitmap = Bitmap.createBitmap(inputBitmap.width, inputBitmap.height, Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(resultMat, transparentBitmap)

        return transparentBitmap
    }

    // ✅ Extract Dominant Background Color from a Reference Image
    private fun getDominantColor(image: Bitmap, tolerance: Int): DoubleArray {
        val mat = Mat()
        Utils.bitmapToMat(image, mat)

        // Convert to HSV
        val hsvMat = Mat()
        Imgproc.cvtColor(mat, hsvMat, Imgproc.COLOR_RGB2HSV)

        // Compute the mean color in HSV
        val meanColor = Core.mean(hsvMat)

        return doubleArrayOf(meanColor.`val`[0], meanColor.`val`[1], meanColor.`val`[2]) // [H, S, V]
    }

    // ✅ Extract Dominant Background Color from the Top-Left 10% Region
    private fun getDominantColorFromTopLeft(image: Bitmap, tolerance: Int): DoubleArray {
        val mat = Mat()
        Utils.bitmapToMat(image, mat)

        val height = mat.rows()
        val width = mat.cols()

        // Define the top-left 10% region
        val regionHeight = (height * 0.1).toInt()
        val regionWidth = (width * 0.1).toInt()
        val roi = Mat(mat, Rect(0, 0, regionWidth, regionHeight))

        // Convert to HSV
        val hsvMat = Mat()
        Imgproc.cvtColor(roi, hsvMat, Imgproc.COLOR_RGB2HSV)

        // Compute the mean color in HSV
        val meanColor = Core.mean(hsvMat)

        return doubleArrayOf(meanColor.`val`[0], meanColor.`val`[1], meanColor.`val`[2]) // [H, S, V]
    }

}
