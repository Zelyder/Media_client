package com.zelyder.mediaclient.domain.repositories

import com.zelyder.mediaclient.domain.models.Media

interface MediaRepository {

    //TODO: return Result<Media> вместо Media
    suspend fun getMedia(id: Int): Media
}