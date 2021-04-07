package com.zelyder.mediaclient.domain.datasources

import com.zelyder.mediaclient.data.network.dto.MediaDto
import com.zelyder.mediaclient.data.network.dto.MediaTypeDto

interface RemoteDataSource {
    suspend fun getMedia(id : Int):MediaDto
    suspend fun getMediaPath(id : Int):String
    suspend fun getMediaType(id : Int): MediaTypeDto
}