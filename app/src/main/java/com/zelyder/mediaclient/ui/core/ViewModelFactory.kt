package com.zelyder.mediaclient.ui.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zelyder.mediaclient.domain.repositories.MediaRepository
import com.zelyder.mediaclient.ui.PlayerViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val mediaRepository: MediaRepository
): ViewModelProvider.Factory  {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = when(modelClass){
        PlayerViewModel::class.java -> PlayerViewModel(mediaRepository)
        else -> throw IllegalArgumentException("$modelClass is not registered ViewModel")
    } as T
}