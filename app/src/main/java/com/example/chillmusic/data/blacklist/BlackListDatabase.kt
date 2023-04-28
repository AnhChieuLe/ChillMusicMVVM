package com.example.chillmusic.data.blacklist

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BlackList::class], version = 1)
abstract class BlackListDatabase : RoomDatabase(){
    abstract fun blackListDao(): BlackListDao

    companion object {
        @Volatile
        private var INSTANCE: BlackListDatabase? = null
        private const val DATABASE_NAME = "blacklist_database"

        fun getDatabase(context: Context): BlackListDatabase {
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BlackListDatabase::class.java,
                    DATABASE_NAME
                ).build()

                INSTANCE = instance
                return instance
            }
        }
    }
}