package com.example.kotlintestapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.kotlintestapp.R
import com.example.kotlintestapp.viewmodel.SettingsViewModel

class ProcessingFragment : Fragment() {
    private val viewModel: SettingsViewModel by viewModels()
    private lateinit var spinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_processing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        spinner = view.findViewById(R.id.spinner_processing_size)
        val sizes = arrayOf("XS", "S", "M", "L", "XL")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sizes)
        spinner.adapter = adapter

        viewModel.imageSize.observe(viewLifecycleOwner) { imageSize ->
            spinner.setSelection(sizes.indexOf(imageSize))
        }
    }
}
