package com.example.chillmusic.data.playlist

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.chillmusic.model.PlayList

@Dao
interface PlayListDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlayList(playList: PlayList)

    @Update
    suspend fun updatePlayList(playList: PlayList)

    @Delete
    suspend fun deletePlayList(playList: PlayList)

    @Query("SELECT * FROM playlist_table ORDER BY name ASC")
    fun getAllPlayList() : LiveData<List<PlayList>>

    @Query("SELECT * FROM playlist_table ORDER BY name ASC")
    fun allPlayList() : List<PlayList>
}