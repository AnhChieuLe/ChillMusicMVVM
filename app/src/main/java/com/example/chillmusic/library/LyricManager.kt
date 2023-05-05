package com.example.chillmusic.library

import androidx.lifecycle.MutableLiveData
import com.example.chillmusic.api.MusicMatchAPI
import com.example.chillmusic.api.model.Track
import com.example.chillmusic.model.Song

class LyricManager {
    val lyrics = MutableLiveData("")
    val tracks = MutableLiveData(listOf<Track>())

    suspend fun setLyric(song: Song, onCompleted: () -> Unit = {}) {
        val response = MusicMatchAPI.apiService.getLyric(
            track = song.title,
            artist = song.artist
        )
        val body = response.body() ?: return
        lyrics.value = body.message.body.lyrics.body
        onCompleted()
    }

    suspend fun setLyric(id: Int, onCompleted: () -> Unit = {}) {
        val response = MusicMatchAPI.apiService.getLyric(id = id)
        val body = response.body() ?: return
        lyrics.value = body.message.body.lyrics.body
        onCompleted()
    }

    suspend fun setTracks(song: Song) {
        val title = song.title.substringBetween("(", ")").substringBefore("-").trim()
        val response = MusicMatchAPI.apiService.getTracks(
            track = title,
            page_size = 6,
        )
        val body = response.body() ?: return
        val tracks = body.message.body.tracks.map { it.track }.filter { it.hasLyrics > 0 }
        this.tracks.value = tracks
    }

    suspend fun setDefaultLyrics(onCompleted: () -> Unit = {}) {
        val value = tracks.value ?: return
        if (value.isEmpty()) return
        if (lyrics.value?.isEmpty() == false) return

        val id = value[0].id
        setLyric(id)
        onCompleted()
    }

    fun clear() {
        lyrics.value = ""
        tracks.value = listOf()
    }
}

fun String.substringBetween(str1: String, str2: String): String {
    return this.substringAfter(str1).substringBefore(str2)
}
