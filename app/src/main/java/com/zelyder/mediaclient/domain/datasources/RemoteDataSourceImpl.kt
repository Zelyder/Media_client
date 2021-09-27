package com.zelyder.mediaclient.domain.datasources

import android.nfc.Tag
import android.util.Log
import com.zelyder.mediaclient.data.BASE_URL
import com.zelyder.mediaclient.data.network.apis.MediaApi
import com.zelyder.mediaclient.data.network.dto.MediaTypeDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOError
import java.io.IOException
import java.lang.NullPointerException

class RemoteDataSourceImpl(private val mediaApi: MediaApi) : RemoteDataSource {

    companion object {
        private const val TAG = "RemoteDataSourceImpl"
    }

    override suspend fun getMediaPath(id: Int): String = withContext(Dispatchers.IO) {
        "${BASE_URL}api/screens/$id/content"
    }

    override suspend fun getMediaType(id: Int): MediaTypeDto? = withContext(Dispatchers.IO) {
        var result: Result<MediaTypeDto> = Result.failure(NullPointerException("Initialization error"))
        mediaApi.getMediaTypeByScreenId(id).enqueue(object : Callback<MediaTypeDto> {
            override fun onResponse(call: Call<MediaTypeDto>, response: Response<MediaTypeDto>) {
                Log.d(TAG, "onResponse: Ok")
                result = if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(IOException("response is not Successful"))
                }
            }

            override fun onFailure(call: Call<MediaTypeDto>, t: Throwable) {
                result = Result.failure(IOException("Request failed"))
            }
        })
        result.getOrNull()
    }
}