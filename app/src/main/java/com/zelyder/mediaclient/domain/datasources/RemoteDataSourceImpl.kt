package com.zelyder.mediaclient.domain.datasources

import android.nfc.Tag
import android.util.Log
import com.zelyder.mediaclient.data.BASE_URL
import com.zelyder.mediaclient.data.network.apis.MediaApi
import com.zelyder.mediaclient.data.network.dto.MediaTypeDto
import com.zelyder.mediaclient.domain.utils.convertToSuspend
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

    override suspend fun getMediaType(id: Int): MediaTypeDto = withContext(Dispatchers.IO) {
        mediaApi.getMediaTypeByScreenId(id).convertToSuspend()
    }
}