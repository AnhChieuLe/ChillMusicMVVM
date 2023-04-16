package com.example.chillmusic.viewmodel

import android.graphics.Bitmap
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.*
import com.example.chillmusic.R
import com.example.chillmusic.model.PlayList
import com.example.chillmusic.enums.Navigation
import com.example.chillmusic.model.MusicStyle
import com.example.chillmusic.model.Song
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.pow
import kotlin.math.roundToInt

object CurrentPlayer : ViewModel() {
    var playList = MutableLiveData(PlayList())
    val name = MutableLiveData("")
    var song = MutableLiveData<Song?>(null)
    val isPlaying = MutableLiveData(false)
    val volume = MutableLiveData(20)
    val progress = MutableLiveData(0)
    val duration = MutableLiveData(0)
    val navigation = MutableLiveData(Navigation.NORMAL)
    val isTouching = MutableLiveData(false)
    var defaultStyle = MusicStyle()
    val style = song.mapWithDefault(defaultStyle) {
        it?.largeAlbumArt?.let { albumArt ->
            MusicStyle(albumArt)
        } ?: defaultStyle
    }
    val isActive = song.map { it != null }

    private fun LiveData<Song?>.mapWithDefault(defaultValue: MusicStyle, mapper: (Song?) -> MusicStyle): LiveData<MusicStyle> {
        return MediatorLiveData<MusicStyle>().apply {
            postValue(defaultValue)
            addSource(this@mapWithDefault) {
                it?.let { postValue(mapper(it)) }
            }
        }
    }

    fun start() {
        isPlaying.postValue(true)
    }

    fun next() {
        song.postValue(playList.value?.getNext(song.value, navigation.value!!))
    }

    fun previous() {
        song.postValue(playList.value?.previous(song.value, navigation.value!!))
    }

    fun pause() {
        isPlaying.postValue(false)
    }

    fun play() {
        isPlaying.postValue(true)
    }

    fun clear() {
        song.postValue(null)
    }

    fun addToNext(id: Long) {
        if (!isActive.value!!) return
        val customPlayList = playList.value
        customPlayList?.addToNext(song.value!!, id)
        playList.postValue(customPlayList)
    }

    fun addToLast(id: Long) {
        if (!isActive.value!!) return
        val customPlayList = playList.value
        customPlayList?.addToLast(id)
        playList.postValue(customPlayList)
    }

    fun newPlayList(id: Long) {
        val customPlayList = PlayList("Danh sách tùy chỉnh", mutableListOf(id))
        playList.postValue(customPlayList)
    }

    fun changeNavigation() {
        navigation.postValue(
            when (navigation.value) {
                Navigation.NORMAL -> Navigation.REPEAT
                Navigation.REPEAT -> Navigation.REPEAT_ONE
                Navigation.REPEAT_ONE -> Navigation.RANDOM
                Navigation.RANDOM -> Navigation.NORMAL
                else -> Navigation.NORMAL
            }
        )
    }
}