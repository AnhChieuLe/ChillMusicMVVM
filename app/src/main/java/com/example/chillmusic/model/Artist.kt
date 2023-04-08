package com.example.chillmusic.model

data class Artist(
    val id: Long,
    val name: String,
    val numOfSong: Int = 0,
) {
}