package com.zelyder.mediaclient.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MediaDto (
    @SerialName("media_type")
    val mediaType: String,
    @SerialName("path")
    val path: String
)
