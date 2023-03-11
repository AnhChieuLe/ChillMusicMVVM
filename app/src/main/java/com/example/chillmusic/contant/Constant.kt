package com.example.chillmusic.contant

import android.util.Log
import com.example.chillmusic.`object`.CurrentPlayer
import com.example.chillmusic.service.*

const val log_tag = "chill_music"

fun log(info: String){
    Log.d(log_tag, info)
}

fun log(action: Int){
    val strAction = when (action) {
        ACTION_PAUSE    ->  "ACTION_PAUSE"
        ACTION_RESUME   ->  "ACTION_RESUME"
        ACTION_CLEAR    ->  "ACTION_CLEAR"
        ACTION_PREVIOUS ->  "ACTION_PREVIOUS"
        ACTION_NEXT     ->  "ACTION_NEXT"
        ACTION_START    ->  "ACTION_START"
        else -> "Con cac"
    }
    log(strAction)
}