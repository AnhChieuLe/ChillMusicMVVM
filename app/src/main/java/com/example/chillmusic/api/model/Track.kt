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
    val hasLyrics: Int,
){
    override fun toString(): String {
        return  "name: $name " +
                "album: $albumName " +
                "artist: $artistName " +
                "hasLyrics: $hasLyrics \n"
    }

    override fun equals(other: Any?): Boolean {
        val track = other as Track
        return this.name == track.name
                && this.albumId == track.albumId
                && this.artistId == track.artistId
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + albumId
        result = 31 * result + albumName.hashCode()
        result = 31 * result + artistId
        result = 31 * result + artistName.hashCode()
        result = 31 * result + hasLyrics
        return result
    }
}
