package com.zelyder.mediaclient.domain.datasources

import com.zelyder.mediaclient.data.BASE_URL
import com.zelyder.mediaclient.data.network.apis.MediaApi
import com.zelyder.mediaclient.data.network.dto.MediaDto
import com.zelyder.mediaclient.data.network.dto.MediaTypeDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RemoteDataSourceImpl(val mediaApi: MediaApi): RemoteDataSource {
    override suspend fun getMedia(id: Int): MediaDto = withContext(Dispatchers.IO){
        mediaApi.getMedia(id)
    }

    override suspend fun getMediaPath(id: Int): String  = withContext(Dispatchers.IO){
        "${BASE_URL}api/screens/$id/content"
    }

    override suspend fun getMediaType(id: Int): MediaTypeDto = withContext(Dispatchers.IO) {
        mediaApi.getMediaTypeByScreenId(id)
    }
}