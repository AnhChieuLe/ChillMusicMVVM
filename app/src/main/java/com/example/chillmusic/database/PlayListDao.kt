package com.example.chillmusic.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PlayListDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlayList(playList: PlayList)

    @Update
    suspend fun updatePlayList(playList: PlayList)

    @Delete
    suspend fun deletePlayList(playList: PlayList)

    @Query("SELECT * FROM playlist_table ORDER BY id ASC")
    fun getAllPlayList() : LiveData<List<PlayList>>

    @Query("SELECT * FROM playlist_table ORDER BY id ASC")
    fun allPlayList() : List<PlayList>
}