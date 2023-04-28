package com.example.chillmusic.data.blacklist

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = BlackList.TABLE_NAME)
data class BlackList(
    @PrimaryKey
    val songId: Long,
){
    companion object {
        const val TABLE_NAME = "blacklist_table"
    }
}
