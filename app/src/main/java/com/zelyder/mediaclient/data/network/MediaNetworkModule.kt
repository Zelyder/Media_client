package com.zelyder.mediaclient.data.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.zelyder.mediaclient.data.BASE_URL
import com.zelyder.mediaclient.data.MEDIA_BASE_URL
import com.zelyder.mediaclient.data.network.apis.MediaApi
import kotlinx.serialization.ExperimentalSerializationApi
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File
import java.nio.file.Files
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
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

//    call: Call<ResponseBody>  = retrofitDownload.downloadRetrofit("retrofit-2.0.0-beta2.jar")
//
//    call.enqueue(new Callback<ResponseBody>() {
//        @Override
//        public void onResponse(Response<ResponseBody> response, Retrofit retrofitParam) {
//            File file = new File("retrofit-2.0.0-beta2.jar");
//            try {
//                file.createNewFile();
//                Files.asByteSink(file).write(response.body().bytes());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public void onFailure(Throwable t) {
//        }
//    });
}