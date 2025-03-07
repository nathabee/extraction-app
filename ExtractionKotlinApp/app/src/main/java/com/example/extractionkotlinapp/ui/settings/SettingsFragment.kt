package com.example.extractionkotlinapp.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.extractionkotlinapp.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    // ✅ Link ViewModel to Fragment
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ✅ Observe ViewModel values and update UI automatically
        settingsViewModel.saveLocation.observe(viewLifecycleOwner) { location ->
            binding.editSaveLocation.setText(location)
        }

        settingsViewModel.defaultSize.observe(viewLifecycleOwner) { size ->
            val sizes = arrayOf("S", "M", "L", "XL")
            val index = sizes.indexOf(size)
            if (index >= 0) {
                binding.spinnerImageSize.setSelection(index)
            }
        }

        // ✅ Setup Spinner for Image Size
        val imageSizes = arrayOf("S", "M", "L", "XL")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, imageSizes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerImageSize.adapter = adapter

        binding.spinnerImageSize.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedSize = imageSizes[position]
                settingsViewModel.updateDefaultSize(selectedSize)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // ✅ Save location when user edits the EditText
        binding.editSaveLocation.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                settingsViewModel.updateSaveLocation(binding.editSaveLocation.text.toString())
            }
        }

        // ✅ Save settings button
        binding.btnSaveSettings.setOnClickListener {
            settingsViewModel.updateSaveLocation(binding.editSaveLocation.text.toString())
            settingsViewModel.updateDefaultSize(binding.spinnerImageSize.selectedItem.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
