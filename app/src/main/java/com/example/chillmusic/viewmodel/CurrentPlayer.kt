package com.example.chillmusic.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.*
import com.example.chillmusic.api.MusicMatchAPI
import com.example.chillmusic.constant.log
import com.example.chillmusic.model.PlayList
import com.example.chillmusic.enums.Navigation
import com.example.chillmusic.library.MusicStyle
import com.example.chillmusic.model.Song
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

object CurrentPlayer : ViewModel() {
    var playList = MutableLiveData(PlayList())
    val name = MutableLiveData("")
    var song = MutableLiveData<Song?>(null)
    val lyric = song.switchMap {
        setLyric(it)
    }
    val isPlaying = MutableLiveData(false)
    val volume = MutableLiveData(70)
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

    private fun setLyric(song: Song?) : LiveData<String>{
        val lyric = MutableLiveData("Đang tải lời bài hát")
        song ?: return lyric
        val handler = CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }

        viewModelScope.launch(handler) {
//            val title = if(song.title.contains("(") && song.title.contains(")"))
//                song.title.substringAfter("(").substringBefore(")")
//            else
//                song.title
//
//            val artists = song.artist.split(",").map { it.trim() }

            val response = MusicMatchAPI.apiService.getLyric(
                track = song.title,
                artist = song.artist
            )
            val lyricResponse = response.body() ?: return@launch
            lyric.postValue(lyricResponse.message.body.lyrics.body)
        }
        return lyric
    }

    private fun getTracks(song: Song?){

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