package com.zelyder.mediaclient.ui

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zelyder.mediaclient.domain.models.Media
import com.zelyder.mediaclient.domain.repositories.MediaRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class PlayerViewModel(private val mediaRepository: MediaRepository) : ViewModel() {
    companion object {
        private const val TAG = "PlayerViewModel"
    }

    private val _media: MutableLiveData<Media> = MutableLiveData()
    val media: LiveData<Media> get() = _media

    private val _connection: MutableLiveData<Boolean> = MutableLiveData()
    val connection: LiveData<Boolean> get() = _connection

    private val _bgUrl: MutableLiveData<String> = MutableLiveData()
    val bgUrl: LiveData<String> get() = _bgUrl

    private val _snackMsg: MutableLiveData<String?> = MutableLiveData()
    val snackMsg: LiveData<String?> = _snackMsg

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

    fun updateBgImage(id: Int) {
        viewModelScope.launch(exceptionHandler) {
            _snackMsg.value = "Обновление картинки по умолчанию"
            _bgUrl.value = mediaRepository.getBgImageUrl(id)
        }
    }

    fun saveImage(image: Bitmap, storageDir: File, imageFileName: String) {
        viewModelScope.launch {
            val successDirCreated = if (!storageDir.exists()) {
                storageDir.mkdir()
            } else {
                true
            }
            if (successDirCreated) {
                val imageFile = File(storageDir, imageFileName)
                val savedImagePath = imageFile.absolutePath
                try {
                    val fOut: OutputStream = FileOutputStream(imageFile)
                    image.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                    val bos = ByteArrayOutputStream()
                    val bitmapdata: ByteArray = bos.toByteArray()
                    fOut.write(bitmapdata)
                    fOut.flush()
                    fOut.close()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                Log.d(TAG, "IMAGE SAVED\n Path = $savedImagePath")
                _snackMsg.value = "Картинка по умолчанию успешно обновлена!"
            } else {
                Log.d(TAG, "SAVE ERROR")
                _snackMsg.value = "Ошибка обновления картинки по умолчанию"
            }
        }
    }
}