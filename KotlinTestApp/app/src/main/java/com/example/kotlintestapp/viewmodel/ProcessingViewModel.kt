package com.example.kotlintestapp.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlintestapp.data.DataStoreManager
import com.example.kotlintestapp.data.ImageSize
import com.example.kotlintestapp.data.ProcessingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProcessingViewModel(private val context: Context, private val repository: ProcessingRepository) : ViewModel() {
    private val dataStoreManager = DataStoreManager(context)

    // ✅ Image Processing States
    private val _inputImageUri = MutableLiveData<Uri?>()
    val inputImageUri: LiveData<Uri?> get() = _inputImageUri

    private val _inputBitmap = MutableLiveData<Bitmap?>()
    val inputBitmap: LiveData<Bitmap?> get() = _inputBitmap

    private val _edgeBitmap = MutableLiveData<Bitmap?>()
    val edgeBitmap: LiveData<Bitmap?> get() = _edgeBitmap

    private val _transparentBitmap = MutableLiveData<Bitmap?>()
    val transparentBitmap: LiveData<Bitmap?> get() = _transparentBitmap

    private val _isProcessing = MutableLiveData(false)
    val isProcessing: LiveData<Boolean> get() = _isProcessing

    private val _threshold1 = MutableLiveData(50f)
    val threshold1: LiveData<Float> get() = _threshold1

    private val _threshold2 = MutableLiveData(150f)
    val threshold2: LiveData<Float> get() = _threshold2

    private val _selectedSize = MutableLiveData<ImageSize>()
    val selectedSize: LiveData<ImageSize> get() = _selectedSize

    private val _savePath = MutableLiveData<String>()
    val savePath: LiveData<String> get() = _savePath

    init {
        loadSettings()
    }

    // ✅ Load Image Size & Save Path from DataStore
    private fun loadSettings() {
        viewModelScope.launch {
            val sizeName = dataStoreManager.imageSize.first()
            _selectedSize.postValue(ImageSize.valueOf(sizeName))

            val path = dataStoreManager.galleryPath.first()
            _savePath.postValue(path)
        }
    }

    // ✅ Save Image Size & Save Path to DataStore
    fun saveSettings(size: ImageSize, path: String) {
        viewModelScope.launch {
            dataStoreManager.saveSettings(size.name, path)
            _selectedSize.postValue(size)
            _savePath.postValue(path)
        }
    }

    // ✅ Set Image URI & Bitmap
    fun setImageUri(uri: Uri?, bitmap: Bitmap?) {
        _inputImageUri.value = uri
        _inputBitmap.value = bitmap
    }

    fun setThreshold1(value: Float) {
        _threshold1.value = value
    }

    fun setThreshold2(value: Float) {
        _threshold2.value = value
    }

    // ✅ Image Processing Logic
    fun processImage() {
        viewModelScope.launch(Dispatchers.IO) {
            _isProcessing.postValue(true)

            val result = repository.processImage(
                _inputBitmap.value,
                _threshold1.value ?: 50f,
                _threshold2.value ?: 150f,
                _selectedSize.value ?: ImageSize.M
            )

            _edgeBitmap.postValue(result.first)
            _transparentBitmap.postValue(result.second)

            _isProcessing.postValue(false)
        }
    }

    // ✅ Save Processed Image to Gallery
    fun saveImage(bitmap: Bitmap, fileName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveBitmapToGallery(context, bitmap, fileName, _selectedSize.value ?: ImageSize.M)
        }
    }

    fun setSelectedSize(size: ImageSize) {
        _selectedSize.postValue(size)
    }

}
