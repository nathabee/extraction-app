package com.example.kotlintestapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kotlintestapp.data.ProcessingRepository

class ProcessingViewModelFactory(private val context: Context, private val repository: ProcessingRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProcessingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProcessingViewModel(context, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


