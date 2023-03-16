package com.example.chillmusic.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chillmusic.`object`.SongsManager

class FlashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SongsManager.requestPermission(actionGranted = {
            SongsManager.setListAudio(this)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, actionDenied = {
            finish()
        })
    }
}