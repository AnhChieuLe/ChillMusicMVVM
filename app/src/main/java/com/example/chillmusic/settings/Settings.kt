package com.example.chillmusic.settings

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.example.chillmusic.network.api.MusixMatchAPI
import com.example.chillmusic.network.crawl.LyricsResource
import com.example.chillmusic.network.crawl.MusixMatch
import com.example.chillmusic.network.crawl.NhacCuaTui

class Settings(application: Application) {
    private val defaultPreferences = PreferenceManager.getDefaultSharedPreferences(application)

    val autoPause: Boolean
        get() = defaultPreferences.getBoolean("auto_pause", true)
    var volume: Int
        get() = defaultPreferences.getInt("volume", 0)
        set(value) { defaultPreferences.edit().putInt("volume", value).apply() }
    val source: LyricsResource
        get() = when(defaultPreferences.getString("lyrics_source", "")){
            "0" -> MusixMatch
            "1" -> MusixMatchAPI
            "2" -> NhacCuaTui
            else -> MusixMatchAPI
        }

    val liveVolume = MutableLiveData(volume)
    val liveSource = MutableLiveData<LyricsResource>(source)

    private val onChange = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
        when(key){
            "volume" -> liveVolume.postValue(volume)
            "lyrics_source" -> liveSource.postValue(source)
        }
    }
    fun register(){
        defaultPreferences.registerOnSharedPreferenceChangeListener(onChange)
    }
    fun unRegister(){
        defaultPreferences.unregisterOnSharedPreferenceChangeListener(onChange)
    }
}