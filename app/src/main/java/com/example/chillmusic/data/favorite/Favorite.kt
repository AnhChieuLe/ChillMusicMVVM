package com.example.chillmusic.data.favorite

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = Favorite.TABLE_NAME)
data class Favorite(
    @PrimaryKey
    val songId: Long,
){
    companion object {
        const val TABLE_NAME = "favorite_table"
    }
}