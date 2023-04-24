package com.example.chillmusic

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager


const val CHANNEL_MEDIA_PLAYER = "CHANNEL_MEDIA_PLAYER"

class MyApplication() : Application() {
    override fun onCreate() {
        super.onCreate()
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