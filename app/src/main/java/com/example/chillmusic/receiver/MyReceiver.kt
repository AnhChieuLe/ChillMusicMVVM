package com.example.chillmusic.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.chillmusic.service.MusicPlayerService

class MyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, it: Intent?) {
        it ?: return
        context ?: return

        val action = it.getIntExtra("action", 0)
        val intent = Intent(context, MusicPlayerService::class.java)
        intent.putExtra("action", action)
        context.startService(intent)
    }
}