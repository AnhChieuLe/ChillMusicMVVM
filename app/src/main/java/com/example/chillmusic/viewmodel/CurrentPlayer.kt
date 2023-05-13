package com.example.chillmusic.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.*
import com.example.chillmusic.network.api.MusixMatchAPI
import com.example.chillmusic.constant.log
import com.example.chillmusic.network.crawl.LyricsResource
import com.example.chillmusic.network.crawl.NhacCuaTui
import com.example.chillmusic.network.crawl.model.Track
import com.example.chillmusic.enums.Navigation
import com.example.chillmusic.library.MusicStyle
import com.example.chillmusic.model.PlayList
import com.example.chillmusic.model.Song
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

object CurrentPlayer : ViewModel() {
    var playList = MutableLiveData(PlayList())
    val name = MutableLiveData("")
    var song = MediatorLiveData<Song?>(null)

    lateinit var lyricsSource: LyricsResource
    val tracks = song.switchMap { getTracks(it) }
    val trackIndex = MediatorLiveData(0)
    var lyric = MediatorLiveData("")

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

    init {
        trackIndex.addSource(tracks){
            setLyrics(it)
        }
        lyric.addSource(trackIndex){
            if(tracks.value?.isEmpty() == true || tracks.value == null) return@addSource

            val track = tracks.value?.get(it) ?: return@addSource
            setLyrics(track)
        }
    }

    private fun LiveData<Song?>.mapWithDefault(defaultValue: MusicStyle, mapper: (Song?) -> MusicStyle): LiveData<MusicStyle> {
        return MediatorLiveData(defaultValue).apply {
            addSource(this@mapWithDefault) {
                postValue(mapper(it))
            }
        }
    }

    private val handle = CoroutineExceptionHandler { coroutineContext, throwable ->
        throwable.printStackTrace()
        coroutineContext.cancel()
    }

    private fun getTracks(song: Song?): MutableLiveData<List<Track>>{
        log(lyricsSource.name)

        val tracks = MutableLiveData<List<Track>>(listOf())
        if(song == null)    return tracks

        viewModelScope.launch(handle + Dispatchers.IO) {
            tracks.postValue(lyricsSource.search(song.apiTitle))
        }

        return tracks
    }

    private fun setLyrics(tracks: List<Track>){
        trackIndex.value = 0
        if(tracks.isEmpty())    return

        viewModelScope.launch(handle + Dispatchers.IO) {
            for((index, track) in tracks.withIndex()){
                try {
                    lyricsSource.getLyrics(track)
                    trackIndex.postValue(index)
                    break
                }catch (e: Exception){ e.printStackTrace() }
            }
        }
    }

    private fun setLyrics(track: Track){
        viewModelScope.launch(handle + Dispatchers.IO) {
            lyric.postValue(lyricsSource.getLyrics(track))
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
        isPlaying.postValue(true)
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