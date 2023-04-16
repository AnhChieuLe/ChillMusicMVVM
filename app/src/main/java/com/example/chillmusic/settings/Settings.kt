package com.example.chillmusic.settings

import android.app.Application
import androidx.preference.PreferenceManager

class Settings(application: Application) {
    private val defaultPreferences = PreferenceManager.getDefaultSharedPreferences(application)

    val autoPause: Boolean
        get() = defaultPreferences.getBoolean("auto_pause", true)
    var volume: Int
        get() = defaultPreferences.getInt("volume", 0)
        set(value) { defaultPreferences.edit().putInt("volume", value).apply() }
}