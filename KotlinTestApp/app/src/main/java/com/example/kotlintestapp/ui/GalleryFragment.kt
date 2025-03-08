package com.example.kotlintestapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.kotlintestapp.R
import com.example.kotlintestapp.viewmodel.SettingsViewModel

class GalleryFragment : Fragment() {
    private val viewModel: SettingsViewModel by viewModels()
    private lateinit var gridView: GridView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gridView = view.findViewById(R.id.grid_view_gallery)

        viewModel.galleryPath.observe(viewLifecycleOwner) { galleryPath ->
            // TODO: Load images from the galleryPath into the GridView dynamically
        }
    }
}
