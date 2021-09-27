package com.zelyder.mediaclient.data.network.apis

import com.zelyder.mediaclient.data.network.dto.MediaTypeDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface MediaApi {
    @GET("api/screens/{Id}/type")
    suspend fun getMediaTypeByScreenId(@Path("Id") id: Int): Call<MediaTypeDto>
}