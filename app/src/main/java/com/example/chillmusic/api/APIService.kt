package com.example.chillmusic.api

import com.example.chillmusic.api.model.LyricResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface APIService {
    @GET("track.search?")
    suspend fun getTracks(
        @Query("apikey") apikey: String = MusicMatchAPI.KEY,
        @Query("q_track") track: String,
        @Query("q_artist") artist: String = "",
        @Query("page_size") page_size: Int = 5,
        @Query("page") page: Int = 1,
        @Query("s_track_rating") rating:String = "desc"
    ): Response<LyricResponse>

    @GET("matcher.lyrics.get?")
    suspend fun getLyric(
        @Query("apikey") apikey: String = MusicMatchAPI.KEY,
        @Query("q_track") track: String,
        @Query("q_artist") artist: String = "",
    ): Response<LyricResponse>
}