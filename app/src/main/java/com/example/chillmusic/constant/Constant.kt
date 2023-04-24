package com.example.chillmusic.constant

import android.util.Log
import com.example.chillmusic.service.*

const val log_tag = "chill_music"

fun log(info: String?){
    Log.d(log_tag, info ?: "")
}

fun log(action: Int){
    val strAction = when (action) {
        ACTION_PAUSE    ->  "ACTION_PAUSE"
        ACTION_PLAY   ->  "ACTION_RESUME"
        ACTION_CLEAR    ->  "ACTION_CLEAR"
        ACTION_SKIP_TO_PREVIOUS ->  "ACTION_PREVIOUS"
        ACTION_SKIP_TO_NEXT     ->  "ACTION_NEXT"
        else -> "Con cac"
    }
    log(strAction)
}