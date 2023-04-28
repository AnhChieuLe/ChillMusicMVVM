package com.example.chillmusic.data.favorite

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(favorite: Favorite)

    @Update
    suspend fun updateSong(favorite: Favorite)

    @Delete
    suspend fun deleteSong(favorite: Favorite)

    @Query("SELECT * FROM ${Favorite.TABLE_NAME}")
    fun getAll(): LiveData<List<Favorite>>
}