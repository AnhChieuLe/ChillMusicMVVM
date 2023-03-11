package com.example.chillmusic.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chillmusic.enums.Navigation
import com.example.chillmusic.model.Song
import java.util.*

class PlayListViewModel : ViewModel(){
    var list: MutableList<Song> = mutableListOf()
    var currentSong = MutableLiveData<Song>()
    var navigation = MutableLiveData(Navigation.NORMAL)

    private val currentPosition get() = list.indexOf(currentSong.value)
    private val isEndOfList get() = currentPosition == list.lastIndex
    private val isFirstOfList get() = currentPosition == 0
    private val history = Stack<Song>()

    fun next() {
        if(history.peek() != currentSong.value)
            history.push(currentSong.value)

        currentSong.value = when (navigation.value) {
            Navigation.NORMAL ->
                if (isEndOfList)
                    null
                else
                    list[currentPosition + 1]

            Navigation.REPEAT ->
                if (isEndOfList)
                    list[0]
                else
                    list[currentPosition + 1]

            Navigation.REPEAT_ONE -> currentSong.value

            Navigation.RANDOM -> list.random()
            else -> currentSong.value
        }
    }

    fun previous(){
        if(history.isNotEmpty()){
            currentSong.value = history.pop()
            return
        }

        currentSong.value = when (navigation.value) {
            Navigation.NORMAL ->
                if (isFirstOfList)
                    null
                else
                    list[currentPosition - 1]

            Navigation.REPEAT ->
                if (isFirstOfList)
                    list[list.lastIndex]
                else
                    list[currentPosition - 1]

            Navigation.REPEAT_ONE -> currentSong.value

            Navigation.RANDOM -> list.random()

            else -> currentSong.value
        }
    }

    fun clear(){
        list.clear()
        currentSong.postValue(null)
    }
}