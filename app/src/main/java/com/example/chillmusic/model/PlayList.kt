package com.example.chillmusic.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.chillmusic.data.MediaStoreManager
import com.example.chillmusic.enums.Navigation
import java.util.*

@Entity(tableName = "playlist_table")
data class PlayList(
    @PrimaryKey
    val name: String = "",
    val songs: MutableList<Long> = mutableListOf()
){
    override fun toString(): String {
        return name
    }

    fun equal(other: PlayList): Boolean {
        return this.songs == other.songs
    }

    @Ignore
    private val history = Stack<Song>()

    fun addToNext(currentSong: Song, id: Long){
        val currentPosition = songs.indexOf(currentSong.id)
        songs.add(currentPosition + 1, id)
    }

    fun addToLast(id: Long){
        songs.add(id)
    }

    fun getNext(song: Song?, navigation: Navigation) : Song?{
        val list = MediaStoreManager.getSongs(*songs.toLongArray()).toMutableList()
        val currentPosition = list.indexOf(song)
        val isEndOfList = currentPosition == list.lastIndex

        history.push(song)

        val nextSong: Song? = when (navigation) {
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
        val list = MediaStoreManager.getSongs(*songs.toLongArray()).toMutableList()
        val previous: Song?

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
}
