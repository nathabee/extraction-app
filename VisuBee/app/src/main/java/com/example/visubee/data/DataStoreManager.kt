package com.example.visubee.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "app_settings")

class DataStoreManager(context: Context) {
    private val dataStore = context.dataStore

    companion object {
        val IMAGE_SIZE_KEY = stringPreferencesKey("imageSize")
        val GALLERY_PATH_KEY = stringPreferencesKey("galleryPath")
    }

    val imageSize: Flow<String> = dataStore.data.map { preferences ->
        preferences[IMAGE_SIZE_KEY] ?: "M"
    }

    val galleryPath: Flow<String> = dataStore.data.map { preferences ->
        preferences[GALLERY_PATH_KEY] ?: "Pictures/KotlinTestApp"
    }

    suspend fun saveSettings(imageSize: String, galleryPath: String) {
        dataStore.edit { preferences ->
            preferences[IMAGE_SIZE_KEY] = imageSize
            preferences[GALLERY_PATH_KEY] = galleryPath
        }
    }
}
