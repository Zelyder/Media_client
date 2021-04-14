package com.zelyder.mediaclient.domain.repositories

import com.zelyder.mediaclient.domain.models.Media

interface MediaRepository {

    suspend fun getMedia(id: Int): Media
}