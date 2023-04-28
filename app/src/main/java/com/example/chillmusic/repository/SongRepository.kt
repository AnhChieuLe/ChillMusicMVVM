package com.example.chillmusic.repository

import com.example.chillmusic.data.song.SongDB
import com.example.chillmusic.data.song.SongDao

class SongRepository(private val dao: SongDao) {
    val allSongDB = dao.getAll()

    suspend fun insert(songDB: SongDB){
        dao.insert(songDB)
    }

    suspend fun update(songDB: SongDB){
        dao.update(songDB)
    }

    suspend fun delete(songDB: SongDB){
        dao.delete(songDB)
    }
}