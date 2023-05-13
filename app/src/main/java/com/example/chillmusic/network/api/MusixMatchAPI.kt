package com.example.chillmusic.network.api

import com.example.chillmusic.network.crawl.LyricsResource
import com.example.chillmusic.network.crawl.model.Track
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object MusixMatchAPI : LyricsResource(){
    private const val baseUrl = "https://api.musixmatch.com/ws/1.1/"
    const val KEY = "42e009aba3a6869507e21faccda666cb"
    override val name: String get() = "MusixMatch API"

    private val converter = GsonConverterFactory.create()
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(converter)
        .build()
    private val apiService get() = retrofit.create<APIService>()

    override suspend fun search(name: String): List<Track> {
        val response = apiService.getTracks(track = name, page_size = 4)
        val body = response.body() ?: return listOf()
        return body.message.body.tracks.map { it.track.toTrack() }
    }

    override suspend fun getLyrics(track: Track): String {
        val response = apiService.getLyric(id = track.id)
        val body = response.body() ?: return ""
        return body.message.body.lyrics.body
    }
}
// http://api.musixmatch.com/ws/1.1/track.search?apikey=42e009aba3a6869507e21faccda666cb&q_artist=den&page_size=3&page=1&s_track_rating=desc
// http://api.musixmatch.com/ws/1.1/track.lyrics.get?apikey=42e009aba3a6869507e21faccda666cb&track_id=160958154