package com.zelyder.mediaclient.data.network.apis

import com.zelyder.mediaclient.data.network.dto.MediaDto
import com.zelyder.mediaclient.data.network.dto.MediaTypeDto
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface MediaApi {
    @GET("api/screens/{Id}/type")
    suspend fun getMediaTypeByScreenId(@Path("Id") id: Int): MediaTypeDto

    @GET("api/screens/{Id}/content")
    suspend fun getMediaByScreenId(@Path("Id") id: Int): String

    @GET("screen/{Id}")
    suspend fun getMedia(@Path("Id") id: Int): MediaDto
}