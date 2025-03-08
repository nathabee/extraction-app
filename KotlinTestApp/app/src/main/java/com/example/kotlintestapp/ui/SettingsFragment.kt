package com.example.kotlintestapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.kotlintestapp.R
import com.example.kotlintestapp.viewmodel.SettingsViewModel

class SettingsFragment : Fragment() {
    private val viewModel: SettingsViewModel by viewModels()
    private lateinit var spinner: Spinner
    private lateinit var editTextPath: EditText
    private lateinit var saveButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        spinner = view.findViewById(R.id.spinner_image_size)
        editTextPath = view.findViewById(R.id.edit_gallery_path)
        saveButton = view.findViewById(R.id.button_save)

        val sizes = arrayOf("XS", "S", "M", "L", "XL")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sizes)
        spinner.adapter = adapter

        viewModel.imageSize.observe(viewLifecycleOwner) { imageSize ->
            spinner.setSelection(sizes.indexOf(imageSize))
        }

        viewModel.galleryPath.observe(viewLifecycleOwner) { galleryPath ->
            editTextPath.setText(galleryPath)
        }

        saveButton.setOnClickListener {
            val selectedSize = sizes[spinner.selectedItemPosition]
            val galleryPath = editTextPath.text.toString()
            viewModel.saveSettings(selectedSize, galleryPath)
        }
    }
}
