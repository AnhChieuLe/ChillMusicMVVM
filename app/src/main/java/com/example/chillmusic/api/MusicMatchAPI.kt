package com.example.chillmusic.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object MusicMatchAPI {
    private const val baseUrl = "https://api.musixmatch.com/ws/1.1/"
    const val KEY = "42e009aba3a6869507e21faccda666cb"
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val apiService get() = retrofit.create<APIService>()
}