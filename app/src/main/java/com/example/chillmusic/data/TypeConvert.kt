package com.example.chillmusic.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TypeConvert {
    private val gson = Gson()
    @TypeConverter
    fun stringToList(string: String): List<Long>{
        val type = (object : TypeToken<List<Long>>(){}).type
        return gson.fromJson(string, type)
    }

    @TypeConverter
    fun listToString(list: List<Long>): String{
        return gson.toJson(list)
    }
}