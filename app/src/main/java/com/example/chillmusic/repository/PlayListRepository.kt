package com.example.chillmusic.repository

import androidx.lifecycle.LiveData
import com.example.chillmusic.data.playlist.PlayListDao
import com.example.chillmusic.model.PlayList

class PlayListRepository(private val dao: PlayListDao) {
    val playLists: LiveData<List<PlayList>> = dao.getAllPlayList()

    suspend fun insert(playList: PlayList){
        dao.insertPlayList(playList)
    }

    suspend fun update(playList: PlayList){
        dao.updatePlayList(playList)
    }

    suspend fun delete(playList: PlayList){
        dao.deletePlayList(playList)
    }
}