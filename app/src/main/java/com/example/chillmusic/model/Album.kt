package com.example.chillmusic.model

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.example.chillmusic.library.MusicStyle
import java.io.Serializable

data class Album(
    val id: Long,
    val name: String,
    val ids: List<Long> = listOf(),
    val albumArt: MutableLiveData<Bitmap> = MutableLiveData()
) : Serializable{
    val style: MusicStyle get() = MusicStyle(albumArt.value)
    val numOfSong: Int get() = ids.size
}