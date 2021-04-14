package com.zelyder.mediaclient.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zelyder.mediaclient.domain.models.Media
import com.zelyder.mediaclient.domain.repositories.MediaRepository
import kotlinx.coroutines.launch

class PlayerViewModel(private val mediaRepository: MediaRepository): ViewModel() {
    private val _media: MutableLiveData<Media> = MutableLiveData()
    val media: LiveData<Media> get() = _media

    fun updateMedia(id: Int) {
        viewModelScope.launch {
            _media.value = mediaRepository.getMedia(id)
        }
    }

}