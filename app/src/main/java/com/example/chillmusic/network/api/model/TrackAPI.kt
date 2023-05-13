package com.example.chillmusic.network.api.model

import com.example.chillmusic.network.crawl.model.Track
import com.google.gson.annotations.SerializedName

class TrackAPI(
    @SerializedName("track_id")
    val id: Int = 0,
    @SerializedName("track_name")
    val name: String,
    @SerializedName("album_id")
    val albumId: Int = 0,
    @SerializedName("album_name")
    val albumName: String = "",
    @SerializedName("artist_id")
    val artistId: Int = 0,
    @SerializedName("artist_name")
    val artistName: String = "",
    @SerializedName("has_lyrics")
    val hasLyrics: Int = 0,
    @SerializedName("track_share_url")
    val url: String = "",
){
    fun toTrack(): Track {
        return Track(
            id = id,
            title = name,
            artist = artistName,
            album = albumName,
            url = url
        )
    }

    override fun toString(): String {
        return  "name: $name " +
                "album: $albumName " +
                "artist: $artistName " +
                "hasLyrics: $hasLyrics \n"
    }

    override fun equals(other: Any?): Boolean {
        val trackAPI = other as TrackAPI
        return this.name == trackAPI.name
                && this.albumId == trackAPI.albumId
                && this.artistId == trackAPI.artistId
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
