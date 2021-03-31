package com.zelyder.mediaclient.data.network

import com.zelyder.mediaclient.data.MEDIA_BASE_URL
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File
import java.nio.file.Files

class MediaNetworkModule {

    private val  mediaRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(MEDIA_BASE_URL)
        .build()

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