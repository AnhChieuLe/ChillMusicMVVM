package com.example.chillmusic.service

import android.content.Intent
import android.media.*
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.lifecycle.LifecycleService
import androidx.media.session.MediaButtonReceiver
import com.example.chillmusic.constant.log
import com.example.chillmusic.model.Song
import com.example.chillmusic.settings.Settings
import com.example.chillmusic.viewmodel.CurrentPlayer
import java.util.*


const val ACTION_PAUSE = 1
const val ACTION_PLAY = 2
const val ACTION_CLEAR = 3
const val ACTION_SKIP_TO_NEXT = 4
const val ACTION_SKIP_TO_PREVIOUS = 5
const val ACTION_SEEK_TO = 6

class MusicPlayerService : LifecycleService() {
    private val mediaPlayer = MediaPlayer()
    private val timer = Timer()
    private val settings by lazy { Settings(application) }
    private val song: Song get() = CurrentPlayer.song.value!!
    private val audioFocus: MusicAudioFocus by lazy { MusicAudioFocus(this, ::handleAction) }
    private val mediaSession: MediaSessionCompat by lazy {
        MediaSessionCompat(this, "main.session.compat").apply {
            setCallback(mediaCallback)
        }
    }
    private val mediaCallback = object : MediaSessionCompat.Callback() {
        fun start(){
            audioFocus.requestFocus(settings.autoPause)
            mediaPlayer.reset()
            mediaPlayer.setDataSource(this@MusicPlayerService, song.uri)
            mediaPlayer.prepare()
            mediaPlayer.setOnPreparedListener {
                it.start()
                sendNotification()
                CurrentPlayer.duration.postValue(it.duration)
            }
        }

        fun pause(){
            mediaPlayer.pause()
            sendNotification()
        }

        fun play(){
            audioFocus.requestFocus(settings.autoPause)
            mediaPlayer.start()
            sendNotification()
        }

        fun clear(){
            stopSelf()
        }

        override fun onPause() {
            super.onPause()
            CurrentPlayer.pause()
        }

        override fun onPlay() {
            super.onPlay()
            CurrentPlayer.play()
        }

        override fun onStop() {
            super.onStop()
            CurrentPlayer.clear()
        }

        override fun onSeekTo(pos: Long) {
            super.onSeekTo(pos)
            mediaPlayer.seekTo(pos.toInt())
        }

        override fun onSkipToNext() {
            super.onSkipToNext()
            CurrentPlayer.next()
        }

        override fun onSkipToPrevious() {
            super.onSkipToPrevious()
            CurrentPlayer.previous()
        }

        override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {
            log(mediaButtonEvent?.action.toString())
            return super.onMediaButtonEvent(mediaButtonEvent)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer.setOnCompletionListener { CurrentPlayer.next() }
        audioFocus.create()
        observer()
        timer.start()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        intent ?: return START_NOT_STICKY

        val action = intent.getIntExtra("action", 0)
        val progress = intent.getIntExtra("progress", 0)
        handleAction(action, progress)

        MediaButtonReceiver.handleIntent(mediaSession, intent)

        return START_NOT_STICKY
    }

    private fun handleAction(action: Int, progress: Int = 0) {
        when (action) {
            ACTION_PAUSE -> CurrentPlayer.pause()
            ACTION_PLAY -> CurrentPlayer.play()
            ACTION_CLEAR -> CurrentPlayer.clear()
            ACTION_SKIP_TO_PREVIOUS -> CurrentPlayer.previous()
            ACTION_SKIP_TO_NEXT -> CurrentPlayer.next()
            ACTION_SEEK_TO -> mediaPlayer.seekTo(progress)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
        mediaPlayer.release()
        audioFocus.abandon()
    }

    private fun observer() {
        CurrentPlayer.volume.observe(this) {
            val volume = it.div(100F)
            mediaPlayer.setVolume(volume, volume)
        }

        CurrentPlayer.isPlaying.observe(this) {
            if (it) mediaCallback.play()
            else mediaCallback.pause()
        }

        CurrentPlayer.song.observe(this) {
            if (it != null) mediaCallback.start()
            else mediaCallback.clear()
        }
    }

    private fun sendNotification() {
        val musicNotification = MusicNotification(
            context = this,
            song = song,
            mediaSession = mediaSession,
            isPlaying = mediaPlayer.isPlaying,
            duration = mediaPlayer.duration.toLong()
        )
        startForeground(1, musicNotification.notification)
    }

    private fun Timer.start() {
        val task = object : TimerTask() {
            override fun run() {
                if (mediaPlayer.isPlaying && !CurrentPlayer.isTouching.value!!)
                    CurrentPlayer.progress.postValue(mediaPlayer.currentPosition)
            }
        }
        scheduleAtFixedRate(task, 0, 200)
    }
}