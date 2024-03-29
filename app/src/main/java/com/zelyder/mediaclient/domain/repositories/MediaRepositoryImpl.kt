package com.zelyder.mediaclient.domain.repositories

import com.zelyder.mediaclient.domain.datasources.RemoteDataSource
import com.zelyder.mediaclient.domain.models.Media
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaRepositoryImpl(private val remoteDataSource: RemoteDataSource): MediaRepository{
    override suspend fun getMedia(id: Int): Media = withContext(Dispatchers.IO){
        Media(remoteDataSource.getMediaPath(id), remoteDataSource.getMediaType(id)?.mediaType ?: "img")
    }

    override suspend fun getBgImageUrl(id: Int): String = withContext(Dispatchers.IO){
        remoteDataSource.getBgImage(id)
    }


}