package com.zelyder.mediaclient.data.network

import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.zelyder.mediaclient.data.BASE_URL
import com.zelyder.mediaclient.data.network.apis.MediaApi
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.create
import java.util.concurrent.TimeUnit

class MediaNetworkModule {

    private val jsonFormat = Json {
        ignoreUnknownKeys = true
    }

    companion object {
        private const val TAG = "MediaNetworkModule"
        private const val TIMEOUT = 30L
    }

    private val httpClient = OkHttpClient.Builder().addInterceptor(
        Interceptor {
            val original = it.request()
            val request = original.newBuilder()
                .build()
            Log.d(TAG, request.toString())
            it.proceed(request)
        }
    )
        .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
        .build()

    @ExperimentalSerializationApi
    private val mediaRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(jsonFormat.asConverterFactory("application/json".toMediaType()))
        .build()

    @ExperimentalSerializationApi
    fun mediaApi(): MediaApi = mediaRetrofit.create()

}