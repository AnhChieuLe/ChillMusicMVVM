package com.example.chillmusic.model

import com.example.chillmusic.enums.MusicState

class MusicPlayer(val song: Song) {
    val state = MusicState.Playing
    val currentProgress = 0
    val maxProgress = 0
    val volume: Int = 0
}