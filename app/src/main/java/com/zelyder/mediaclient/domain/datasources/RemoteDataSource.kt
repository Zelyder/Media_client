package com.zelyder.mediaclient.domain.datasources

import com.zelyder.mediaclient.data.network.dto.MediaTypeDto

interface RemoteDataSource {
    suspend fun getMediaPath(id : Int):String
    suspend fun getMediaType(id : Int): MediaTypeDto
}