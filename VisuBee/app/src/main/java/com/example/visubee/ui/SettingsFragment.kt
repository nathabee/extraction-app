package com.example.visubee.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.visubee.R
import com.example.visubee.viewmodel.SettingsViewModel
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate

class SettingsFragment : Fragment() {
    private val viewModel: SettingsViewModel by viewModels()
    private lateinit var spinner: Spinner
    private lateinit var editTextPath: EditText
    private lateinit var saveButton: Button
    private lateinit var choosePathButton: Button
    private lateinit var themeSpinner: Spinner

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


        themeSpinner = view.findViewById(R.id.spinner_theme)

        // ✅ Load themes from arrays.xml BEFORE using them
        val themeOptions: Array<String> = resources.getStringArray(R.array.theme_options)
        val themeValues: Array<String> = resources.getStringArray(R.array.theme_values)

        // ✅ Set up the theme spinner adapter
        val themeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, themeOptions)
        themeSpinner.adapter = themeAdapter

        // ✅ Load saved theme preference safely
        val sharedPref = requireActivity().getSharedPreferences("settings", Context.MODE_PRIVATE)
        val savedTheme = sharedPref.getString("theme_preference", "system") ?: "system"

        // ✅ Set the spinner selection correctly
        val selectedIndex = themeValues.indexOf(savedTheme)
        themeSpinner.setSelection(if (selectedIndex != -1) selectedIndex else 2) // Default to "Follow System"

        // ✅ Prevent unnecessary theme change on first load
        var firstLoad = true
        themeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (firstLoad) {
                    firstLoad = false
                    return
                }
                val selectedTheme = themeValues[position]
                saveThemePreference(selectedTheme)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
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

    // ✅ Function to Save and Apply Theme Preference
    private fun saveThemePreference(theme: String) {
        val sharedPref = requireActivity().getSharedPreferences("settings", Context.MODE_PRIVATE)
        sharedPref.edit().putString("theme_preference", theme).apply()

        when (theme) {
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "system" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

}
