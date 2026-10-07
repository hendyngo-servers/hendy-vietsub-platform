package com.hendy.vietsub.models

import kotlinx.serialization.Serializable

@Serializable
data class SubtitleItem(
    val id: String,
    val startTimeMs: Long,
    val endTimeMs: Long,
    var originalText: String,
    var translatedText: String
)
