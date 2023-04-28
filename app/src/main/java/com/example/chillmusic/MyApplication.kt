package com.example.chillmusic

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.example.chillmusic.model.PlayList
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


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