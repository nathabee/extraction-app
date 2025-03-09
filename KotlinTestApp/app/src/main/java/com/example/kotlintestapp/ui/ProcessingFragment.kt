package com.example.kotlintestapp.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.activity.result.contract.ActivityResultContracts
import com.example.kotlintestapp.R
import com.example.kotlintestapp.data.ImageSize
import com.example.kotlintestapp.data.ProcessingRepository
import com.example.kotlintestapp.viewmodel.ProcessingViewModel
import com.example.kotlintestapp.viewmodel.ProcessingViewModelFactory

class ProcessingFragment : Fragment() {
    private val viewModel: ProcessingViewModel by viewModels {
        ProcessingViewModelFactory(requireContext(), ProcessingRepository())
    }

    private lateinit var imageViewInput: ImageView
    private lateinit var imageViewBackground: ImageView
    private lateinit var imageViewEdge: ImageView
    private lateinit var imageViewTransparent: ImageView
    private lateinit var spinnerSize: Spinner
    private lateinit var threshold1SeekBar: SeekBar
    private lateinit var threshold2SeekBar: SeekBar
    private lateinit var btnProcess: Button
    private lateinit var btnSave: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_processing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize UI elements
        imageViewInput = view.findViewById(R.id.img_input)
        imageViewBackground = view.findViewById(R.id.img_background)
        imageViewEdge = view.findViewById(R.id.img_edge)
        imageViewTransparent = view.findViewById(R.id.img_transparent)
        spinnerSize = view.findViewById(R.id.spinner_image_size)
        threshold1SeekBar = view.findViewById(R.id.seekbar_threshold1)
        threshold2SeekBar = view.findViewById(R.id.seekbar_threshold2)
        btnProcess = view.findViewById(R.id.btn_process)
        btnSave = view.findViewById(R.id.btn_save)

        // Image Picker
        val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                viewModel.setImageUri(it, null)
            }
        }

        val bgPickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                viewModel.setImageUri(it, null)
            }
        }

        view.findViewById<Button>(R.id.btn_select_image).setOnClickListener { imagePickerLauncher.launch("image/*") }
        view.findViewById<Button>(R.id.btn_select_background).setOnClickListener { bgPickerLauncher.launch("image/*") }

        // Update Image Views
        viewModel.inputImageUri.observe(viewLifecycleOwner, Observer { uri ->
            uri?.let { imageViewInput.setImageURI(it) }
        })

        viewModel.edgeBitmap.observe(viewLifecycleOwner, Observer { bitmap ->
            bitmap?.let { imageViewEdge.setImageBitmap(it) }
        })

        viewModel.transparentBitmap.observe(viewLifecycleOwner, Observer { bitmap ->
            bitmap?.let { imageViewTransparent.setImageBitmap(it) }
        })

        // Set Image Size Spinner
        val sizes = ImageSize.values().map { it.name }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sizes)
        spinnerSize.adapter = adapter

        spinnerSize.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedSize = ImageSize.valueOf(sizes[position])
                viewModel.setSelectedSize(selectedSize)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Set Threshold SeekBars
        threshold1SeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.setThreshold1(progress.toFloat())
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        threshold2SeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.setThreshold2(progress.toFloat())
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Process Image Button
        btnProcess.setOnClickListener { viewModel.processImage() }

        // Save Image Button
        btnSave.setOnClickListener { viewModel.saveImage(viewModel.edgeBitmap.value!!, "Processed_Image") }
    }
}
