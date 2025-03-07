package com.example.extractionkotlinapp.ui.settings

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    private val editor = prefs.edit()

    private val _saveLocation = MutableLiveData(prefs.getString("save_location", "Pictures/MyApp") ?: "Pictures/MyApp")
    val saveLocation: LiveData<String> = _saveLocation

    private val _defaultSize = MutableLiveData(prefs.getString("default_size", "M") ?: "M")
    val defaultSize: LiveData<String> = _defaultSize

    fun updateSaveLocation(newLocation: String) {
        _saveLocation.value = newLocation
        editor.putString("save_location", newLocation).apply()
    }

    fun updateDefaultSize(newSize: String) {
        _defaultSize.value = newSize
        editor.putString("default_size", newSize).apply()
    }
}
