package com.zelyder.mediaclient.data.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.zelyder.mediaclient.data.BASE_URL
import com.zelyder.mediaclient.data.network.apis.MediaApi
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.create

class MediaNetworkModule {

    private val jsonFormat = Json {
        ignoreUnknownKeys = true
    }


    @ExperimentalSerializationApi
    private val mediaRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(jsonFormat.asConverterFactory("application/json".toMediaType()))
        .build()

    @ExperimentalSerializationApi
    fun mediaApi(): MediaApi = mediaRetrofit.create()

}