package com.example.extractionkotlinapp

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.io.IOException
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown


// Image Size Enum
enum class ImageSize(val width: Int, val height: Int) {
    S(150, 150),
    M(300, 300),
    L(600, 600),
    XL(1200, 1200)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageProcessingScreen(paddingValues: PaddingValues) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var inputImageUri by remember { mutableStateOf<Uri?>(null) }
    var inputBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var edgeBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var transparentBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var threshold1 by remember { mutableFloatStateOf(50f) }
    var threshold2 by remember { mutableFloatStateOf(150f) }

    var bgImageUri by remember { mutableStateOf<Uri?>(null) }
    var bgBitmap by remember { mutableStateOf<Bitmap?>(null) }

    var selectedSize by remember { mutableStateOf(ImageSize.M) }
    var expanded by remember { mutableStateOf(false) }

    val prefs = remember { context.getSharedPreferences("app_settings", Context.MODE_PRIVATE) }
    val defaultSizeName = prefs.getString("default_size", "M") ?: "M"
    selectedSize = ImageSize.valueOf(defaultSizeName)
    val saveLocation = prefs.getString("save_location", "Pictures/MyApp") ?: "Pictures/MyApp"


    // Image Picker
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            inputImageUri = uri
            if (uri != null) {
                try {
                    inputBitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
                } catch (e: IOException) {
                    Log.e("ImageProcessingScreen", "Error loading image", e)
                }
            }
        }
    )

    // Background Picker
    val bgImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            bgImageUri = uri
            if (uri != null) {
                try {
                    bgBitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
                } catch (e: IOException) {
                    Log.e("ImageProcessingScreen", "Error loading background image", e)
                }
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { imagePickerLauncher.launch("image/*") }) { Text("Select Image") }
            Button(onClick = { bgImagePickerLauncher.launch("image/*") }) { Text("Select Background") }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            inputBitmap?.let {
                Image(bitmap = it.asImageBitmap(), contentDescription = "Input Image", modifier = Modifier.size(150.dp))
            }
            bgBitmap?.let {
                Image(bitmap = it.asImageBitmap(), contentDescription = "Background Image", modifier = Modifier.size(150.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            edgeBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Edge Image",
                    modifier = Modifier
                        .size(150.dp)
                        .clickable { saveBitmapToGallery(context, it, "Edge_Image", selectedSize) }
                )
            }
            transparentBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Transparent Image",
                    modifier = Modifier
                        .size(150.dp)
                        .clickable { saveBitmapToGallery(context, it, "Transparent_Image", selectedSize) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = threshold1.toString(),
            onValueChange = { threshold1 = it.toFloatOrNull() ?: 0f },
            label = { Text("Threshold 1") }
        )

        Slider(value = threshold1, onValueChange = { threshold1 = it }, valueRange = 0f..255f, modifier = Modifier.fillMaxWidth())

        OutlinedTextField(
            value = threshold2.toString(),
            onValueChange = { threshold2 = it.toFloatOrNull() ?: 0f },
            label = { Text("Threshold 2") }
        )

        Slider(value = threshold2, onValueChange = { threshold2 = it }, valueRange = 0f..255f, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))


        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = selectedSize.name,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.menuAnchor(), // ✅ Ensures proper focus handling
                label = { Text("Select Image Size") },
                trailingIcon = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                    }
                }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                ImageSize.values().forEach { size ->
                    DropdownMenuItem(
                        text = { Text(size.name) },
                        onClick = {
                            selectedSize = size
                            expanded = false
                        }
                    )
                }
            }
        }


        Button(
            onClick = {
                coroutineScope.launch {
                    isProcessing = true
                    val result = processImage(inputBitmap, threshold1, threshold2, selectedSize)
                    edgeBitmap = result.first
                    transparentBitmap = result.second
                    isProcessing = false
                }
            },
            enabled = !isProcessing
        ) {
            Text("Process Image")
        }

        if (isProcessing) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}

suspend fun processImage(
    inputBitmap: Bitmap?,
    threshold1: Float,
    threshold2: Float,
    selectedSize: ImageSize
): Pair<Bitmap?, Bitmap?> = withContext(Dispatchers.IO) {
    if (inputBitmap == null) return@withContext Pair(null, null)

    val resizedBitmap = resizeBitmap(inputBitmap, selectedSize)

    val imgMat = Mat(resizedBitmap.height, resizedBitmap.width, CvType.CV_8UC4)
    bitmapToMat(resizedBitmap, imgMat)

    // Convert to grayscale and detect edges
    Imgproc.cvtColor(imgMat, imgMat, Imgproc.COLOR_RGBA2GRAY)
    Imgproc.Canny(imgMat, imgMat, threshold1.toDouble(), threshold2.toDouble())

    // Convert back to Bitmap for edges
    val edgeBitmap = Bitmap.createBitmap(resizedBitmap.width, resizedBitmap.height, Bitmap.Config.ARGB_8888)
    if (imgMat.channels() == 1) {
        Imgproc.cvtColor(imgMat, imgMat, Imgproc.COLOR_GRAY2RGBA)
    }
    matToBitmap(imgMat, edgeBitmap)

    // Generate transparent image
    val transparentBitmap = resizeBitmap(removeBackground(inputBitmap), selectedSize)

    return@withContext Pair(edgeBitmap, transparentBitmap)
}



fun matToBitmap(mat: Mat, bitmap: Bitmap) {
    val width = mat.cols()
    val height = mat.rows()

    // Check Mat type and adjust accordingly
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


// Function to remove background (Simple HSV-based approach)
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

fun saveBitmapToGallery(context: Context, bitmap: Bitmap, fileName: String, selectedSize: ImageSize) {
    val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    val savePath = prefs.getString("save_location", "Pictures/MyApp") ?: "Pictures/MyApp"

    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "$fileName-${selectedSize.name}.png")
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        put(MediaStore.Images.Media.RELATIVE_PATH, savePath)
    }

    val resolver = context.contentResolver
    val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    imageUri?.let {
        resolver.openOutputStream(it)?.use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            Toast.makeText(context, "Saved in $savePath", Toast.LENGTH_SHORT).show()
        }
    } ?: Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
}



fun resizeBitmap(bitmap: Bitmap, targetSize: ImageSize): Bitmap {
    val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
    val (targetWidth, targetHeight) = if (aspectRatio > 1) {
        Pair(targetSize.width, (targetSize.width / aspectRatio).toInt())
    } else {
        Pair((targetSize.height * aspectRatio).toInt(), targetSize.height)
    }

    return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
}
