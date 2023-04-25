package com.zelyder.mediaclient.domain.datasources

import com.zelyder.mediaclient.data.network.dto.MediaTypeDto

interface RemoteDataSource {
    fun getMediaPath(id : Int):String
    suspend fun getMediaType(id : Int): MediaTypeDto?
    fun getBgImage(id: Int): String
}