package com.example.chillmusic.service

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager

class MusicAudioFocus(
    private val context: Context,
    private val handleAction: (Int) -> Unit
) {
    private lateinit var audioManager: AudioManager
    private lateinit var audioAttributes: AudioAttributes
    private lateinit var focusRequest: AudioFocusRequest
    private var audioFocusRequest: Int = 0
    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        val action = when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN,
            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT,
            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE,
            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK,
            -> ACTION_PLAY

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT,
            AudioManager.AUDIOFOCUS_LOSS,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK,
            -> ACTION_PAUSE

            else -> -10
        }
        handleAction(action)
    }

    fun request(){
        audioFocusRequest = audioManager.requestAudioFocus(focusRequest)
    }

    fun abandon(){
        audioManager.abandonAudioFocusRequest(focusRequest)
    }

    fun requestFocus(autoPause: Boolean){
        if (autoPause)
            audioFocusRequest = audioManager.requestAudioFocus(focusRequest)
        else
            audioManager.abandonAudioFocusRequest(focusRequest)
    }

    fun create(){
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(audioAttributes)
            .setAcceptsDelayedFocusGain(true)
            .setOnAudioFocusChangeListener(audioFocusChangeListener)
            .build()
        audioFocusRequest = audioManager.requestAudioFocus(focusRequest)
    }
}