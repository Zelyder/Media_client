package com.zelyder.mediaclient.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateResult(
    @SerialName("msg")
    val msg: String,
    @SerialName("screen_number")
    val screen_number: Int
)
