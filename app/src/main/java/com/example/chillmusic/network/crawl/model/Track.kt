package com.example.chillmusic.network.crawl.model

data class Track(
    val id: Int = 0,
    val title: String,
    val artist: String,
    val url: String,
    val album: String = "",
    val albumArtUrl: String = "",
): Comparable<Track>{
    override fun compareTo(other: Track): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        val track = other as Track
        return  title == track.title
                &&  artist == track.artist
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + url.hashCode()
        result = 31 * result + album.hashCode()
        result = 31 * result + albumArtUrl.hashCode()
        return result
    }
}
