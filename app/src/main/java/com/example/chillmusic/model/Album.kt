package com.example.chillmusic.model

import android.graphics.Bitmap

data class Album(
    val id: Long,
    val name: String,
    val numOfSong: Int = 0,
    val albumArt: Bitmap?
) {
}