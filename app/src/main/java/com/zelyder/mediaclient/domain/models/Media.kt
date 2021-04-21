package com.zelyder.mediaclient.domain.models

import java.time.Duration

data class Media(
    val url: String,
    val type: String,
    val duration: Long
)
