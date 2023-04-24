package com.example.chillmusic.api.model

import com.google.gson.annotations.SerializedName

data class Body(
    @SerializedName("track_list")
    val tracks: List<APIResponse> = listOf(),
    @SerializedName("lyrics")
    val lyrics: Lyric,
)