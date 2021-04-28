package com.zelyder.mediaclient.domain.datasources

import com.zelyder.mediaclient.domain.models.Media

class TestDataSource {

    fun getImageMedia(): Media {
        return Media(
            "https://docs.microsoft.com/ru-ru/xamarin/android/platform/fragments/creating-a-fragment-images/fragment-lifecycle.png",
            "img",
        10
        )
    }
    fun getGifMedia(): Media {
        return Media(
            "http://i.kinja-img.com/gawker-media/image/upload/s--B7tUiM5l--/gf2r69yorbdesguga10i.gif",
            "gif",
            10
        )
    }

    fun getVideoMedia(): Media {
        return Media(
            "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4",
            "vid",
            10
        )
    }

    fun getLargeVideoMedia(): Media {
        return Media(
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            "vid",
            10
        )
    }
}