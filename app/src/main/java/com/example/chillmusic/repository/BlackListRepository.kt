package com.example.chillmusic.repository

import com.example.chillmusic.data.blacklist.BlackList
import com.example.chillmusic.data.blacklist.BlackListDao
import com.example.chillmusic.data.favorite.Favorite
import com.example.chillmusic.data.favorite.FavoriteDao

class BlackListRepository (private val dao: BlackListDao){
    val blackList = dao.getAll()

    suspend fun insert(blackList: BlackList){
        dao.insert(blackList)
    }

    suspend fun delete(blackList: BlackList){
        dao.delete(blackList)
    }

    suspend fun update(blackList: BlackList){
        dao.update(blackList)
    }
}