package com.example.visubee.ui

//import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
//import androidx.lifecycle.Observer
import com.example.visubee.R
import com.example.visubee.data.ImageSize
import com.example.visubee.data.ProcessingRepository
import com.example.visubee.viewmodel.ProcessingViewModel
import com.example.visubee.viewmodel.ProcessingViewModelFactory
import java.io.IOException
//import android.util.Log
import androidx.appcompat.app.AlertDialog
//import androidx.compose.ui.text.intl.Locale
import com.example.visubee.viewmodel.SettingsViewModel
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
//import org.opencv.android.OpenCVLoader
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import androidx.documentfile.provider.DocumentFile




class ProcessingFragment : Fragment() {

    private val viewModel: ProcessingViewModel by viewModels {
        ProcessingViewModelFactory(requireActivity().application, ProcessingRepository()) // ✅ FIXED
    }

    private val settingsViewModel: SettingsViewModel by activityViewModels()

    private lateinit var progressBar: ProgressBar
    private lateinit var imageViewInput: ImageView
    private lateinit var imageViewBackground: ImageView
    private lateinit var imageSizeSpinner: Spinner
    private lateinit var threshold1SeekBar: SeekBar
    private lateinit var threshold2SeekBar: SeekBar
    private lateinit var toleranceSeekBar: SeekBar
    private lateinit var brightnessSeekBar: SeekBar

    private var inputImageUri: Uri? = null
    private var backgroundImageUri: Uri? = null

    private lateinit var filenameEditText: EditText
    private var currentGalleryPath: String = ""



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_processing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // UI Elements
        val btnSelectImage = view.findViewById<Button>(R.id.btn_select_image)
        val btnSelectBackground = view.findViewById<Button>(R.id.btn_select_background)
        val btnProcess = view.findViewById<Button>(R.id.btn_process)
        val btnSave = view.findViewById<Button>(R.id.btn_save)

        imageViewInput = view.findViewById(R.id.img_input)
        imageViewBackground = view.findViewById(R.id.img_background)
        imageSizeSpinner = view.findViewById(R.id.spinner_image_size)
        progressBar = view.findViewById(R.id.progress_processing)

        threshold1SeekBar = view.findViewById(R.id.seekbar_threshold1)
        threshold2SeekBar = view.findViewById(R.id.seekbar_threshold2)
        toleranceSeekBar = view.findViewById(R.id.seekbar_tolerance)
        brightnessSeekBar = view.findViewById(R.id.seekbar_brightness)

        lifecycleScope.launch {
            settingsViewModel.galleryPath.observe(viewLifecycleOwner) { path ->
                // now you always have the latest path available:
                currentGalleryPath = path
            }
        }

        // ✅ Populate Image Size Spinner
        val imageSizes = ImageSize.values().map { it.name }
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, imageSizes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        imageSizeSpinner.adapter = adapter

        // ✅ Observe Default Settings
        viewModel.selectedSize.observe(viewLifecycleOwner) { size ->
            val index = imageSizes.indexOf(size.name)
            if (index >= 0) imageSizeSpinner.setSelection(index)
        }

        viewModel.threshold1.observe(viewLifecycleOwner) {
            threshold1SeekBar.progress = it.toInt()
        }

        viewModel.threshold2.observe(viewLifecycleOwner) {
            threshold2SeekBar.progress = it.toInt()
        }

        viewModel.tolerance.observe(viewLifecycleOwner) {
            toleranceSeekBar.progress = it
        }

        viewModel.brightness.observe(viewLifecycleOwner) {
            brightnessSeekBar.progress = it
        }

        // ✅ Observe Processing State
        viewModel.isProcessing.observe(viewLifecycleOwner) { isProcessing ->
            progressBar.visibility = if (isProcessing) View.VISIBLE else View.GONE
        }

        // ✅ Select Image Button
        btnSelectImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        // ✅ Select Background Button
        btnSelectBackground.setOnClickListener {
            backgroundPickerLauncher.launch("image/*")
        }

        // ✅ Process Image Button
        btnProcess.setOnClickListener {
            progressBar.visibility = View.VISIBLE

            viewModel.setThreshold1(threshold1SeekBar.progress.toFloat())
            viewModel.setThreshold2(threshold2SeekBar.progress.toFloat())
            viewModel.setTolerance(toleranceSeekBar.progress)
            viewModel.setBrightness(brightnessSeekBar.progress)

            val selectedSize = ImageSize.valueOf(imageSizeSpinner.selectedItem.toString())
            viewModel.setSelectedSize(selectedSize)

            viewModel.processImage()
        }

        // ✅ Observe Image Processing Results
        viewModel.edgeBitmap.observe(viewLifecycleOwner) { bitmap ->
            view.findViewById<ImageView>(R.id.img_edge).setImageBitmap(bitmap)
        }

        viewModel.transparentBitmap.observe(viewLifecycleOwner) { bitmap ->
            view.findViewById<ImageView>(R.id.img_transparent).setImageBitmap(bitmap)
        }

        // ✅ Save Image Button
        btnSave.setOnClickListener {
            viewModel.edgeBitmap.value?.let {
                viewModel.saveImage(it, "Processed_Image")
            }
        }

         // Reference to TextViews (make sure you add these in the XML)
        val textThreshold1 = view.findViewById<TextView>(R.id.text_threshold1_value)
        val textThreshold2 = view.findViewById<TextView>(R.id.text_threshold2_value)
        val textTolerance = view.findViewById<TextView>(R.id.text_tolerance_value)
        val textBrightness = view.findViewById<TextView>(R.id.text_brightness_value)

// ✅ Update text values when SeekBars change
        threshold1SeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textThreshold1.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        threshold2SeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textThreshold2.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        toleranceSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textTolerance.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        brightnessSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textBrightness.text = String.format("%d", progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })


        // ✅ Select Background from Input Image
        val btnSelectBgRegion = view.findViewById<Button>(R.id.btn_select_bg_region)
        btnSelectBgRegion.setOnClickListener {
            if (inputImageUri != null) {
                selectBackgroundRegion(inputImageUri!!)
            } else {
                Toast.makeText(requireContext(), "Select an input image first", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        filenameEditText = view.findViewById(R.id.filenameEditText)

        val originalName = inputImageUri?.lastPathSegment ?: "default_filename"
        val selectedSize = imageSizeSpinner.selectedItem.toString()

        // Prefill filename:
        filenameEditText.setText(String.format("%s_%s_edge.png", originalName, selectedSize))

        btnSave.setOnClickListener {
            saveProcessedImages()
        }




    }


    // ✅ Ensure filename updates when selecting a new image
    private fun updateFilenameEditText() {
        val originalName = inputImageUri?.lastPathSegment?.substringBeforeLast(".") ?: "default_filename"
        val selectedSize = imageSizeSpinner.selectedItem.toString()
        filenameEditText.setText(String.format(Locale.getDefault(), "%s_%s", originalName, selectedSize))
    }

    // ✅ Image Picker
    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                inputImageUri = it
                imageViewInput.setImageURI(it)
                loadBitmap(it, isBackground = false)
                updateFilenameEditText() // 🔹 Update filename when new image is selected
            }
        }


    // ✅ Background Picker
    private val backgroundPickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                backgroundImageUri = it
                imageViewBackground.setImageURI(it)
                loadBitmap(it, isBackground = true)
            }
        }

    // ✅ Convert Uri to Bitmap and Update ViewModel
    private fun loadBitmap(uri: Uri, isBackground: Boolean) {
        try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            if (isBackground) {
                viewModel.setBackgroundImage(uri, bitmap)
            } else {
                viewModel.setImageUri(uri, bitmap)
            }
        } catch (e: IOException) {
            Toast.makeText(requireContext(), "Error loading image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun selectBackgroundRegion(uri: Uri) {
        try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            val imageView = ImageView(requireContext()).apply {
                setImageBitmap(bitmap)
                scaleType = ImageView.ScaleType.FIT_CENTER
            }

            val selectionView = SelectionOverlayView(requireContext(), bitmap)

            val container = FrameLayout(requireContext()).apply {
                addView(imageView)
                addView(selectionView)
            }

            AlertDialog.Builder(requireContext())
                .setTitle("Select Background Region")
                .setView(container)
                .setPositiveButton("OK") { _, _ ->
                    val selectedRegion = selectionView.getSelectedRegion()
                    if (selectedRegion != null) {
                        imageViewBackground.setImageBitmap(selectedRegion)
                        viewModel.setBackgroundImage(null, selectedRegion)
                    } else {
                        Toast.makeText(requireContext(), "No selection made", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                .setNegativeButton("Cancel", null)
                .create()
                .show()

        } catch (e: IOException) {
            Toast.makeText(requireContext(), "Error loading image", Toast.LENGTH_SHORT).show()
        }
    }


    private fun saveImage(bitmap: android.graphics.Bitmap, filename: String): Boolean {
        val galleryPath = settingsViewModel.galleryPath.value

        if (galleryPath.isNullOrEmpty()) {
            Toast.makeText(context, "No save location selected!", Toast.LENGTH_SHORT).show()
            return false
        }

        return try {
            val uri = Uri.parse(galleryPath) // Convert stored path to URI
            val pickedDir = DocumentFile.fromTreeUri(requireContext(), uri)

            if (pickedDir != null && pickedDir.canWrite()) {
                val file = pickedDir.createFile("image/png", filename)
                file?.let {
                    requireContext().contentResolver.openOutputStream(it.uri)?.use { outputStream ->
                        bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, outputStream)
                    }
                    Toast.makeText(context, "Image saved: $filename", Toast.LENGTH_LONG).show()
                    return true
                }
            }

            Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
            false
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error saving image", Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun saveProcessedImages() {
        val baseFilename = filenameEditText.text.toString().trim()
        val selectedSize = imageSizeSpinner.selectedItem.toString()

        val edgeFilename = "${baseFileNameFrom(baseFilename)}_${selectedSize}_edge.png"
        val transparentFilename = "${baseFileNameFrom(baseFilename)}_${selectedSize}_transparent.png"

        val edgeBitmap = viewModel.edgeBitmap.value
        val transparentBitmap = viewModel.transparentBitmap.value

        if (edgeBitmap == null || transparentBitmap == null) {
            Toast.makeText(context, "Processed images not available", Toast.LENGTH_SHORT).show()
            return
        }

        val edgeSaved = saveImage(edgeBitmap, edgeFilename)
        val transparentSaved = saveImage(transparentBitmap, transparentFilename)

        if (edgeSaved && transparentSaved) {
            Toast.makeText(context, "Images saved successfully!", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Failed to save images.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun baseFileNameFrom(path: String): String {
        return File(path).nameWithoutExtension
    }

}
