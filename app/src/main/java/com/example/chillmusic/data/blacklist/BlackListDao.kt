package com.example.chillmusic.data.blacklist

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface BlackListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(blackList: BlackList)

    @Delete
    suspend fun delete(blackList: BlackList)

    @Update
    suspend fun update(blackList: BlackList)

    @Query("SELECT * FROM ${BlackList.TABLE_NAME}")
    fun getAll(): LiveData<List<BlackList>>
}