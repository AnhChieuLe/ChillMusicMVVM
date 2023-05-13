package com.example.chillmusic.network.api.model

import com.google.gson.annotations.SerializedName

data class Body(
    @SerializedName("track_list")
    val tracks: List<TrackContainer> = listOf(),
    @SerializedName("lyrics")
    val lyrics: Lyric = Lyric(0, ""),
)