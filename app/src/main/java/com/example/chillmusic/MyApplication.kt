package com.example.chillmusic

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.example.chillmusic.network.api.MusixMatchAPI
import com.example.chillmusic.constant.log
import com.example.chillmusic.network.crawl.MusixMatch
import com.example.chillmusic.network.crawl.NhacCuaTui
import com.example.chillmusic.enums.LyricsSource
import com.example.chillmusic.settings.Settings
import com.example.chillmusic.viewmodel.CurrentPlayer


const val CHANNEL_MEDIA_PLAYER = "CHANNEL_MEDIA_PLAYER"

class MyApplication() : Application() {
    private val settings: Settings by lazy { Settings(this) }

    override fun onCreate() {
        super.onCreate()
        settings.register()
        createNotificationChanel()
    }

    private fun createNotificationChanel() {
        val channel = NotificationChannel (
            CHANNEL_MEDIA_PLAYER,
            getString(R.string.channel_media_player),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.setSound(null, null)
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}