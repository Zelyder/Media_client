package com.zelyder.mediaclient.data.network.apis

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface MediaApi {

    @GET("screen/{Id}")
    suspend fun getMediaByScreenId(@Path("Id") id: Int): Call<ResponseBody>
}