package com.example.chillmusic.api.model

import com.google.gson.annotations.SerializedName

class Track(
    @SerializedName("track_id")
    val id: Int,
    @SerializedName("track_name")
    val name: String,
    @SerializedName("album_id")
    val albumId: Int,
    @SerializedName("album_name")
    val albumName: String,
    @SerializedName("artist_id")
    val artistId: Int,
    @SerializedName("artist_name")
    val artistName: String,
    @SerializedName("has_lyrics")
    val hasLyrics: Boolean,
)
