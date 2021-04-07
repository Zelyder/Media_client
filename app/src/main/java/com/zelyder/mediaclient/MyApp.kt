package com.zelyder.mediaclient

import android.app.Application
import android.content.Context
import androidx.fragment.app.Fragment
import com.zelyder.mediaclient.data.network.MediaNetworkModule
import com.zelyder.mediaclient.domain.datasources.RemoteDataSource
import com.zelyder.mediaclient.domain.datasources.RemoteDataSourceImpl
import com.zelyder.mediaclient.domain.repositories.MediaRepository
import com.zelyder.mediaclient.domain.repositories.MediaRepositoryImpl
import com.zelyder.mediaclient.ui.core.ViewModelFactory
import com.zelyder.mediaclient.ui.core.ViewModelFactoryProvider
import kotlinx.serialization.ExperimentalSerializationApi

class MyApp: Application(), ViewModelFactoryProvider {

    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var mediaRepository: MediaRepository

    @ExperimentalSerializationApi
    override fun onCreate() {
        super.onCreate()

        initRepositories()


    }


    @ExperimentalSerializationApi
    private fun initRepositories() {

        val remoteDateSource = RemoteDataSourceImpl(MediaNetworkModule().mediaApi())

        mediaRepository = MediaRepositoryImpl(remoteDateSource)

        viewModelFactory = ViewModelFactory(mediaRepository)
    }

    override fun viewModelFactory(): ViewModelFactory  = viewModelFactory
}

fun Context.viewModelFactoryProvider() = (applicationContext as MyApp)

fun Fragment.viewModelFactoryProvider() = requireContext().viewModelFactoryProvider()