package com.example.visubee.ui

import android.content.Intent
import android.net.Uri
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
import com.example.visubee.R
import com.example.visubee.viewmodel.SettingsViewModel
import androidx.activity.result.contract.ActivityResultContracts

class SettingsFragment : Fragment() {
    private val viewModel: SettingsViewModel by viewModels()
    private lateinit var spinner: Spinner
    private lateinit var editTextPath: EditText
    private lateinit var saveButton: Button
    private lateinit var choosePathButton: Button

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
        choosePathButton = view.findViewById(R.id.button_choose_path)
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

        // Launch folder picker when button is clicked
        choosePathButton.setOnClickListener {
            folderPickerLauncher.launch(null)
        }

        // Save settings only when Save button is clicked
        saveButton.setOnClickListener {
            val selectedSize = sizes[spinner.selectedItemPosition]
            val galleryPath = editTextPath.text.toString()

            if (galleryPath.isEmpty()) {
                editTextPath.error = "Please choose a valid save location!"
                return@setOnClickListener
            }

            viewModel.saveSettings(selectedSize, galleryPath)
        }
    }



    // Folder picker launcher (Updates UI but does NOT save settings)
    private val folderPickerLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri: Uri? ->
            uri?.let {
                requireContext().contentResolver.takePersistableUriPermission(
                    it, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )

                val selectedPath = it.toString()
                editTextPath.setText(selectedPath) // Update UI immediately

                // 🔹 Update LiveData so it's reflected in UI
                viewModel.updateGalleryPath(selectedPath) // ✅ Add this method in `SettingsViewModel`
            }
        }

}
