package com.vietsub.models

import kotlinx.serialization.Serializable

@Serializable
data class SubtitleItem(
    val id: Int,
    val startMs: Long,
    val endMs: Long,
    val text: String
)
