package com.zelyder.mediaclient.domain.datasources

import com.zelyder.mediaclient.domain.models.Media

class TestDataSource {

    fun getImageMedia(): Media {
        return Media(
            "https://docs.microsoft.com/ru-ru/xamarin/android/platform/fragments/creating-a-fragment-images/fragment-lifecycle.png",
            "img"
        )
    }

    fun getVideoMedia(): Media {
        return Media(
            "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4",
            "video"
        )
    }
}