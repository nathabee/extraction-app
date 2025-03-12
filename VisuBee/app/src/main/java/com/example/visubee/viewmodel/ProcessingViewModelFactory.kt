package com.example.visubee.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.visubee.data.ProcessingRepository

class ProcessingViewModelFactory(
    private val application: Application, // 🔹 Use Application instead of Context
    private val repository: ProcessingRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProcessingViewModel::class.java)) {
            return ProcessingViewModel(application, repository) as T // 🔹 Pass Application
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}



