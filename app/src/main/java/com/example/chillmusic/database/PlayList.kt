package com.example.chillmusic.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlayList(
    val name: String = "",
    val songs: List<Int> = listOf()
){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    override fun toString(): String {
        return name
    }

    fun equal(other: PlayList): Boolean {
        return this.name == other.name && this.songs == other.songs
    }

}
