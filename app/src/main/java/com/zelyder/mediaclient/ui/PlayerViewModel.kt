package com.zelyder.mediaclient.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlayerViewModel: ViewModel() {
    private val _mediaUrl: MutableLiveData<String> = MutableLiveData()
    val mediaUrl: LiveData<String> get() = _mediaUrl

    fun updateUrl() {
        _mediaUrl.value = ""//TODO: вставить url из репозитория
    }

}