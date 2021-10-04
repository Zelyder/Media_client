package com.zelyder.mediaclient.ui

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zelyder.mediaclient.domain.models.Media
import com.zelyder.mediaclient.domain.repositories.MediaRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.launch

class PlayerViewModel(private val mediaRepository: MediaRepository): ViewModel() {
    companion object {
        private const val TAG = "PlayerViewModel"
    }

    private val _media: MutableLiveData<Media> = MutableLiveData()
    val media: LiveData<Media> get() = _media
    private val _connection: MutableLiveData<Boolean> = MutableLiveData()
    val connection: LiveData<Boolean> get() = _connection

    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        val name = coroutineContext[CoroutineName] ?: "unknown"
        val error = throwable.stackTraceToString()
        Log.e(TAG, "$name : \n $error")
        _connection.value = false
    }

    fun updateMedia(id: Int) {
        viewModelScope.launch(exceptionHandler) {
            _media.value = mediaRepository.getMedia(id)
            _connection.value = true
        }
    }

}