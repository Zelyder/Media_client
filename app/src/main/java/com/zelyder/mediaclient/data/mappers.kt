package com.zelyder.mediaclient.data

import com.zelyder.mediaclient.data.network.dto.MediaDto
import com.zelyder.mediaclient.domain.models.Media

fun MediaDto.toMedia(): Media = Media(
    url = url,
    type = mediaType,
    duration = duration
)