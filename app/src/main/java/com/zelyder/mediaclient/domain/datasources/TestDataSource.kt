package com.zelyder.mediaclient.domain.datasources

import com.zelyder.mediaclient.data.network.dto.MediaDto
import com.zelyder.mediaclient.data.network.dto.MediaTypeDto
import com.zelyder.mediaclient.domain.models.Media

class TestDataSource: RemoteDataSource {

    fun getImageMedia(): Media {
        return Media(
            "https://docs.microsoft.com/ru-ru/xamarin/android/platform/fragments/creating-a-fragment-images/fragment-lifecycle.png",
            "img"
        )
    }
    fun getGifMedia(): Media {
        return Media(
            "http://i.kinja-img.com/gawker-media/image/upload/s--B7tUiM5l--/gf2r69yorbdesguga10i.gif",
            "gif"
        )
    }

    fun getVideoMedia(): Media {
        return Media(
            "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4",
            "vid"
        )
    }

    fun getLargeVideoMedia(): Media {
        return Media(
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            "vid"
        )
    }

    override suspend fun getMedia(id: Int): MediaDto {
        return MediaDto("img", "https://docs.microsoft.com/ru-ru/xamarin/android/platform/fragments/creating-a-fragment-images/fragment-lifecycle.png", 10)
    }

    override suspend fun getMediaPath(id: Int): String {
        return "https://docs.microsoft.com/ru-ru/xamarin/android/platform/fragments/creating-a-fragment-images/fragment-lifecycle.png"
    }

    override suspend fun getMediaType(id: Int): MediaTypeDto {
        return MediaTypeDto("img")
    }
}