package com.example.chillmusic.data.song

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SongDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(songDB: SongDB)

    @Update
    suspend fun update(songDB: SongDB)

    @Delete
    suspend fun delete(songDB: SongDB)

    @Query("SELECT * FROM ${SongDB.TABLE_NAME}")
    fun getAll(): LiveData<SongDB>
}