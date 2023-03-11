package com.example.chillmusic.service

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.*
import android.media.AudioManager.OnAudioFocusChangeListener
import android.media.session.MediaSession
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer
import androidx.media.app.NotificationCompat.MediaStyle
import androidx.media.session.MediaButtonReceiver
import com.example.chillmusic.CHANNEL_MEDIA_PLAYER
import com.example.chillmusic.R
import com.example.chillmusic.activity.MusicPlayerActivity
import com.example.chillmusic.contant.log
import com.example.chillmusic.model.Song
import com.example.chillmusic.`object`.CurrentPlayer
import com.example.chillmusic.receiver.MyReceiver
import java.util.*


const val ACTION_PAUSE = 1
const val ACTION_RESUME = 2
const val ACTION_CLEAR = 3
const val ACTION_NEXT = 4
const val ACTION_PREVIOUS = 5
const val ACTION_START = 6
const val ACTION_SEEK_TO = 7

class MusicPlayerService : Service() {
    private val mediaPlayer = MediaPlayer()
    private val song: Song get() = CurrentPlayer.song.value!!
    private val isPlaying get() = CurrentPlayer.isPlaying.value ?: false
    private val volume get() = CurrentPlayer.volume.value?.div(100F) ?: 0F
    private val image: Bitmap get() = song.smallImage
    private val timer = Timer()

    private val mediaSessionCompat: MediaSessionCompat by lazy {
        MediaSessionCompat(this, "tag").apply {
            setCallback(mediaSessionCallback)
        }
    }
    private val mediaSessionCallback = object : MediaSessionCompat.Callback() {
        override fun onPause() {
            super.onPause()
            handleAction(ACTION_PAUSE)
        }

        override fun onPlay() {
            super.onPlay()
            handleAction(ACTION_RESUME)
        }

        override fun onSeekTo(pos: Long) {
            super.onSeekTo(pos)
            mediaPlayer.seekTo(pos.toInt())
        }

        override fun onSkipToNext() {
            super.onSkipToNext()
            handleAction(ACTION_NEXT)
        }

        override fun onSkipToPrevious() {
            super.onSkipToPrevious()
            handleAction(ACTION_PREVIOUS)
        }
    }

    private val mediaMetadata
        get() = MediaMetadata.Builder().apply {
            putLong(MediaMetadata.METADATA_KEY_DURATION, /*song.duration.toLong()*/ -1L)
            putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, song.smallImage)
            putString(MediaMetadata.METADATA_KEY_TITLE, song.title)
            putString(MediaMetadata.METADATA_KEY_ARTIST, song.artist)
            putString(MediaMetadata.METADATA_KEY_ALBUM, song.album)
        }.build()

    private lateinit var audioManager: AudioManager
    private lateinit var audioAttributes: AudioAttributes
    private lateinit var focusRequest: AudioFocusRequest
    private var audioFocusRequest: Int = 0

    private val audioFocusChangeListener = OnAudioFocusChangeListener { focusChange ->
        log("Focus: $focusChange")
        val action = when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> ACTION_RESUME
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> ACTION_PAUSE
            AudioManager.AUDIOFOCUS_LOSS -> ACTION_PAUSE
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> ACTION_PAUSE
            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE -> ACTION_RESUME
            else -> -10
        }
        handleAction(action)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        log("onCreate")
        mediaPlayer.setOnCompletionListener { CurrentPlayer.next() }
        audioManager = (getSystemService(Context.AUDIO_SERVICE) as? AudioManager)!!
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
        registerLivedata()
        timer.start()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log("onStartCommand")
        val action = intent?.getIntExtra("action", 0) ?: 0
        if (action == ACTION_SEEK_TO) {
            val progress = intent?.getIntExtra("progress", 0) ?: 0
            mediaPlayer.seekTo(progress)
        }
        handleAction(action)

        MediaButtonReceiver.handleIntent(mediaSessionCompat, intent)
        return START_NOT_STICKY
    }

    private fun handleAction(action: Int) {
        when (action) {
            ACTION_PAUSE -> CurrentPlayer.pause()
            ACTION_RESUME -> CurrentPlayer.resume()
            ACTION_CLEAR -> CurrentPlayer.clear()
            ACTION_PREVIOUS -> CurrentPlayer.previous()
            ACTION_NEXT -> CurrentPlayer.next()
            ACTION_START -> CurrentPlayer.start()
        }
        log(action)
    }

    private fun startMusic() {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(applicationContext, song.contentUri)
        mediaPlayer.prepare()
        mediaPlayer.setOnPreparedListener {
            it.startMusic()
            sendNotification()
            CurrentPlayer.duration.postValue(it.duration)
        }
        log("Start music")
    }

    private fun pauseMusic() {
        if (!mediaPlayer.isPlaying) return
        mediaPlayer.pause()
        sendNotification()
        log("Pause Music")
    }

    private fun resumeMusic() {
        if (mediaPlayer.isPlaying) return
        mediaPlayer.startMusic()
        sendNotification()
        log("Resume Music")
    }

    private fun MediaPlayer.startMusic() {
        audioFocusRequest = audioManager.requestAudioFocus(focusRequest)
        this.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
        unRegisterLivedata()
        mediaPlayer.release()
        audioManager.abandonAudioFocusRequest(focusRequest)
    }

    private val observerVolume = Observer<Int> {
        mediaPlayer.setVolume(volume, volume)
    }

    private val observerPlaying = Observer<Boolean> {
        if (it) resumeMusic()
        else pauseMusic()
    }

    private val observerSong = Observer<Song> {
        startMusic()
    }

    private val observerActive = Observer<Boolean> {
        if (!it) stopSelf()
    }

    private fun registerLivedata() {
        CurrentPlayer.volume.observeForever(observerVolume)
        CurrentPlayer.isPlaying.observeForever(observerPlaying)
        CurrentPlayer.song.observeForever(observerSong)
        CurrentPlayer.isActive.observeForever(observerActive)
    }

    private fun unRegisterLivedata() {
        CurrentPlayer.volume.removeObserver(observerVolume)
        CurrentPlayer.isPlaying.removeObserver(observerPlaying)
        CurrentPlayer.song.removeObserver(observerSong)
        CurrentPlayer.isActive.removeObserver(observerActive)
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

    private fun sendNotification() {
        val intent = Intent(this, MusicPlayerActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val playbackState = PlaybackStateCompat.Builder().apply {
            setState(PlaybackStateCompat.STATE_PLAYING, mediaPlayer.currentPosition.toLong(), 1F)
            setActions(
                PlaybackStateCompat.ACTION_SEEK_TO
                        or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                        or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                        or PlaybackStateCompat.ACTION_PAUSE
                        or PlaybackStateCompat.ACTION_PLAY
                        or PlaybackStateCompat.ACTION_PLAY_PAUSE
            )
        }.build()

        mediaSessionCompat.apply {
            setMetadata(MediaMetadataCompat.fromMediaMetadata(mediaMetadata))
            isActive = true
            setPlaybackState(playbackState)
        }

        val style = MediaStyle()
            .setShowActionsInCompactView(1, 3)
            .setMediaSession(mediaSessionCompat.sessionToken)

        val notification = NotificationCompat.Builder(this, CHANNEL_MEDIA_PLAYER).apply {
            setSmallIcon(R.drawable.music_note)
            setContentTitle(song.title)
            setContentText(if (song.artist == "") getString(R.string.unknown) else song.artist)
            setLargeIcon(image)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setContentIntent(pendingIntent)
            addAction(R.drawable.previous, "previous", getPendingIntent(ACTION_PREVIOUS))
            if (isPlaying)
                addAction(R.drawable.pause, "pause", getPendingIntent(ACTION_PAUSE))
            else
                addAction(R.drawable.play, "resume", getPendingIntent(ACTION_RESUME))
            addAction(R.drawable.next, "next", getPendingIntent(ACTION_NEXT))
            addAction(R.drawable.clear, "clear", getPendingIntent(ACTION_CLEAR))
            setStyle(style)
        }.build()

        startForeground(1, notification)
    }

    //Service -> Broadcast -> Service
    private fun getPendingIntent(action: Int): PendingIntent {
        val intent = Intent(this, MyReceiver::class.java).apply { putExtra("action", action) }
        return PendingIntent.getBroadcast(
            applicationContext,
            action,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }
}