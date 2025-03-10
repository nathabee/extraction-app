package com.example.visubee.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.visubee.data.DataStoreManager
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStoreManager = DataStoreManager(application)

    val imageSize = dataStoreManager.imageSize.asLiveData()
    val galleryPath = dataStoreManager.galleryPath.asLiveData()

    fun saveSettings(imageSize: String, galleryPath: String) {
        viewModelScope.launch {
            dataStoreManager.saveSettings(imageSize, galleryPath)
        }
    }
}
