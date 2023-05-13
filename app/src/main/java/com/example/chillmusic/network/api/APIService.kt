package com.example.chillmusic.network.api

import com.example.chillmusic.network.api.model.APIResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface APIService {
    @GET("track.search?")
    suspend fun getTracks(
        @Query("apikey")            apikey: String = MusixMatchAPI.KEY,
        @Query("q_track")           track: String,
        @Query("q_artist")          artist: String = "",
        @Query("page_size")         page_size: Int = 20,
        @Query("page")              page: Int = 1,
        @Query("s_track_rating")    rating:String = "desc"
    ): Response<APIResponse>

    @GET("matcher.lyrics.get?")
    suspend fun getLyric(
        @Query("apikey") apikey: String = MusixMatchAPI.KEY,
        @Query("q_track") track: String,
        @Query("q_artist") artist: String = "",
    ): Response<APIResponse>

    @GET("track.lyrics.get?")
    suspend fun getLyric(
        @Query("apikey") apikey: String = MusixMatchAPI.KEY,
        @Query("track_id") id: Int
    ): Response<APIResponse>
}