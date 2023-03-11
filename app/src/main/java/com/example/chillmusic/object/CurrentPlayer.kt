package com.example.chillmusic.`object`

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.chillmusic.enums.Navigation
import com.example.chillmusic.model.MusicStyle
import com.example.chillmusic.model.PlayList
import com.example.chillmusic.model.Song
import com.example.chillmusic.viewmodel.CurrentPlayerViewModel
import com.example.chillmusic.viewmodel.PlayListViewModel

object CurrentPlayer {
    var playList = PlayList()
    var song = MutableLiveData<Song>()
    val isPlaying = MutableLiveData(false)
    val isActive = MutableLiveData(false)
    val volume = MutableLiveData(50)
    val progress = MutableLiveData(0)
    val duration = MutableLiveData(0)
    val navigation = MutableLiveData(Navigation.NORMAL)
    val isTouching = MutableLiveData(false)

    fun start() {
        isActive.postValue(true)
        isPlaying.postValue(true)
    }

    fun next() {
        song.postValue(playList.getNext(song.value, navigation.value!!))
    }

    fun previous() {
        song.postValue(playList.previous(song.value, navigation.value!!))
    }

    fun pause() {
        isPlaying.postValue(false)
    }

    fun resume() {
        isPlaying.postValue(true)
    }

    fun clear() {
        isActive.postValue(false)
    }
}