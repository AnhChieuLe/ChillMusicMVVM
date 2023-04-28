package com.example.chillmusic.repository

import com.example.chillmusic.data.favorite.Favorite
import com.example.chillmusic.data.favorite.FavoriteDao

class FavoriteRepository (private val dao: FavoriteDao){
    val favorites = dao.getAll()

    suspend fun insert(favorite: Favorite){
        dao.insertSong(favorite)
    }

    suspend fun delete(favorite: Favorite){
        dao.deleteSong(favorite)
    }

    suspend fun update(favorite: Favorite){
        dao.updateSong(favorite)
    }
}