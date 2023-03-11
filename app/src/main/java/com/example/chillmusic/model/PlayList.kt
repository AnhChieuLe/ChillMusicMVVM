package com.example.chillmusic.model

import androidx.lifecycle.MutableLiveData
import com.example.chillmusic.enums.Navigation
import java.util.Stack

class PlayList(
    var list: MutableList<Song> = mutableListOf(),
) {
    private val history = Stack<Song>()

    fun getNext(song: Song?, navigation: Navigation) : Song?{
        val currentPosition = list.indexOf(song)
        val isEndOfList = currentPosition == list.lastIndex

        var nextSong: Song? = null

        history.push(song)

        nextSong = when (navigation) {
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

            Navigation.REPEAT_ONE -> song

            Navigation.RANDOM -> list.random()
        }
        return nextSong
    }

    fun previous(song: Song?, navigation: Navigation): Song?{
        var previous: Song? = null

        val currentPosition = list.indexOf(song)
        val isFirstOfList = currentPosition == 0

        if(history.isNotEmpty()){
            previous = history.pop()
            return previous
        }

        previous = when (navigation) {
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

            Navigation.REPEAT_ONE -> song

            Navigation.RANDOM -> list.random()
        }

        return previous
    }

    fun clear(){
        list.clear()
    }
}