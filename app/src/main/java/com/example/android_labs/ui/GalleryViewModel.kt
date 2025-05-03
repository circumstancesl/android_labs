package com.example.android_labs.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_labs.data.GalleryRepository
import com.example.android_labs.data.ImageItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val repository: GalleryRepository
) : ViewModel() {
    private val _images = MutableLiveData<List<ImageItem>>()
    val images: LiveData<List<ImageItem>> = _images

    fun loadImages() = viewModelScope.launch {
        _images.value = repository.getImagesWithDescriptions()
    }

    fun updateDescription(mediaId: Long, newDescription: String) = viewModelScope.launch {
        repository.updateDescription(mediaId, newDescription)
        loadImages()
    }
}