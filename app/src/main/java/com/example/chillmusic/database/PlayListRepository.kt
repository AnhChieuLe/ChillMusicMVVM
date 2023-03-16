package com.example.chillmusic.database

import android.content.Context
import androidx.lifecycle.LiveData

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