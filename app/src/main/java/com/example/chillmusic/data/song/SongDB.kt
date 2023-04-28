package com.example.chillmusic.data.song

import androidx.room.Entity

@Entity(primaryKeys = ["id", "isFavorite", "isInBlackList"], tableName = SongDB.TABLE_NAME)
data class SongDB(
    val id: Long,
    val isFavorite: Boolean = false,
    val isInBlackList: Boolean = false,
){
    companion object {
        const val TABLE_NAME = "song_table"
    }
}
