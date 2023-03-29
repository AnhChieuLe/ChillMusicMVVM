package com.example.chillmusic.repository

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager

class SettingRepository(application: Application) {
    private val defaultPreferences = PreferenceManager.getDefaultSharedPreferences(application)

    val autoPause: Boolean
        get() = defaultPreferences.getBoolean("auto_pause", true)
    var volume: Int
        get() = defaultPreferences.getInt("volume", 0)
        set(value) { defaultPreferences.edit().putInt("volume", value).apply() }

}