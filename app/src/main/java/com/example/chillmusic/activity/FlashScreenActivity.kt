package com.example.chillmusic.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chillmusic.`object`.ListSongManager

class FlashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ListSongManager.requestPermission(actionGranted = {
            ListSongManager.setListAudio(this)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, actionDenied = {
            finish()
        })
    }
}