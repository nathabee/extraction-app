package com.example.visubee.viewmodel

import android.app.Application
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.visubee.data.DataStoreManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.catch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStoreManager = DataStoreManager(application)

    // ✅ LiveData for the latest gallery path
    private val _galleryPath = MutableLiveData<String>()
    val galleryPath: LiveData<String> get() = _galleryPath

    val imageSize = dataStoreManager.imageSize.asLiveData()

    // ✅ Default gallery path inside app's external storage
    private val appGalleryDefaultPath: String =
        application.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath ?: ""

    init {
        // ✅ Load settings from DataStore when ViewModel is created
        viewModelScope.launch {
            try {
                _galleryPath.value = dataStoreManager.galleryPath.first() // ✅ Must be inside coroutine
            } catch (e: Exception) {
                _galleryPath.value = appGalleryDefaultPath // ✅ Use default if DataStore is empty
            }
        }
    }

    // ✅ Update path when user picks a folder
    fun updateGalleryPath(newPath: String) {
        _galleryPath.value = newPath
    }

    // ✅ Save settings only when user confirms

    fun saveSettings(imageSize: String, galleryPath: String) {
        viewModelScope.launch {
            dataStoreManager.saveSettings(imageSize, galleryPath)
            _galleryPath.postValue(galleryPath) // Update LiveData after saving
        }
    }

}
