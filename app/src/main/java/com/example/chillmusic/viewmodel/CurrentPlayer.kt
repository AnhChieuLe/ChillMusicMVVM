package com.example.chillmusic.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.*
import com.example.chillmusic.api.MusicMatchAPI
import com.example.chillmusic.api.model.Track
import com.example.chillmusic.enums.Navigation
import com.example.chillmusic.library.MusicStyle
import com.example.chillmusic.model.PlayList
import com.example.chillmusic.model.Song
import kotlinx.coroutines.*

object CurrentPlayer : ViewModel() {
    var playList = MutableLiveData(PlayList())
    val name = MutableLiveData("")
    var song = MediatorLiveData<Song?>(null)
    val lyric = MediatorLiveData("")
    val isPlaying = MutableLiveData(false)
    val volume = MutableLiveData(20)
    val progress = MutableLiveData(0)
    val duration = MutableLiveData(0)
    val navigation = MutableLiveData(Navigation.NORMAL)
    val isTouching = MutableLiveData(false)
    var defaultStyle = MusicStyle()
    val isActive = song.map { it != null }
    val style = song.mapWithDefault(defaultStyle) {
        if(it?.liveAlbumArt?.value == null)     defaultStyle
        else    MusicStyle(it.liveAlbumArt.value)
    }

    private fun LiveData<Song?>.mapWithDefault(defaultValue: MusicStyle, mapper: (Song?) -> MusicStyle): LiveData<MusicStyle> {
        return MediatorLiveData(defaultValue).apply {
            addSource(this@mapWithDefault) {
                postValue(mapper(it))
            }
        }
    }

    fun start() {
        isPlaying.postValue(true)
    }

    fun next() {
        song.postValue(playList.value?.getNext(song.value, navigation.value!!))
        isPlaying.postValue(true)
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

    private lateinit var countDownTimer: CountDownTimer
    val timeUntilFinished: MutableLiveData<Long> = MutableLiveData(-1L)
    val isCountDown = timeUntilFinished.map { it > 0 }

    fun startCountDown(time: Long){
        timeUntilFinished.postValue(time)
        if(this::countDownTimer.isInitialized)  countDownTimer.cancel()
        countDownTimer = object : CountDownTimer(time, 1000L){
            override fun onTick(millisUntilFinished: Long) {
                timeUntilFinished.postValue(millisUntilFinished)
            }

            override fun onFinish() {
                timeUntilFinished.postValue(-1L)
                pause()
            }
        }
        countDownTimer.start()
    }

    fun stopCountDown(){
        countDownTimer.cancel()
        timeUntilFinished.postValue(-1L)
    }
}