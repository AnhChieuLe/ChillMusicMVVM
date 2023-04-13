package com.example.chillmusic.model

data class Artist(
    val id: Long,
    val name: String,
    val ids: List<Long> = listOf(),
) {
    val numOfSong: Int get() = ids.size
}