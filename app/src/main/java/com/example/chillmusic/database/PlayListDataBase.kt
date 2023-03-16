package com.example.chillmusic.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [PlayList::class], version = 1)
@TypeConverters(TypeConvert::class)
abstract class PlayListDataBase : RoomDatabase() {
    abstract fun playListDao(): PlayListDao

    companion object {
        @Volatile
        private var INSTANCE: PlayListDataBase? = null

        fun getDatabase(context: Context): PlayListDataBase{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlayListDataBase::class.java,
                    "user_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}