package com.example.visubee.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.visubee.R
import com.example.visubee.data.ImageSize
import com.example.visubee.data.ProcessingRepository
import com.example.visubee.viewmodel.ProcessingViewModel
import com.example.visubee.viewmodel.ProcessingViewModelFactory
import java.io.IOException
import android.util.Log
import androidx.appcompat.app.AlertDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.opencv.android.OpenCVLoader

class ProcessingFragment : Fragment() {

    private val viewModel: ProcessingViewModel by viewModels {
        ProcessingViewModelFactory(requireContext(), ProcessingRepository())
    }

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
                textBrightness.text = progress.toString()
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
    }

    // ✅ Image Picker
    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                inputImageUri = it
                imageViewInput.setImageURI(it)
                loadBitmap(it, isBackground = false)
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
}
