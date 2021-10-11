package com.zelyder.mediaclient.ui

import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zelyder.mediaclient.data.CACHED_IMAGE_NAME
import com.zelyder.mediaclient.domain.models.Media
import com.zelyder.mediaclient.domain.repositories.MediaRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

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
            // TODO: handle Result form get media
            _media.value = mediaRepository.getMedia(id)
            _connection.value = true
        }
    }

    fun saveImage(image: Bitmap): String?{
        var savedImagePath: String? = null
        viewModelScope.launch(exceptionHandler) {
            val storageDir = File(
                Environment.getDownloadCacheDirectory()
                    .toString() + "/last_data"
            )
            var success = true
            if (!storageDir.exists()) {
                success = storageDir.mkdirs()
            }
            if (success) {
                val imageFile = File(storageDir, CACHED_IMAGE_NAME)
                savedImagePath = imageFile.absolutePath
                try {
                    val fOut: OutputStream = FileOutputStream(imageFile)
                    image.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                    fOut.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                Log.d(TAG, "IMAGE SAVED\n Path = $savedImagePath")
            }

        }
        return savedImagePath
    }

}