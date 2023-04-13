package com.example.chillmusic.service

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.*
import android.media.AudioManager.OnAudioFocusChangeListener
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.KeyEvent
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer
import androidx.media.app.NotificationCompat.MediaStyle
import androidx.media.session.MediaButtonReceiver
import com.example.chillmusic.CHANNEL_MEDIA_PLAYER
import com.example.chillmusic.R
import com.example.chillmusic.activity.MainActivity
import com.example.chillmusic.constant.log
import com.example.chillmusic.model.Song
import com.example.chillmusic.receiver.MyReceiver
import com.example.chillmusic.settings.Settings
import com.example.chillmusic.viewmodel.CurrentPlayer
import java.util.*


const val ACTION_PAUSE = 1
const val ACTION_PLAY = 2
const val ACTION_CLEAR = 3
const val ACTION_SKIP_TO_NEXT = 4
const val ACTION_SKIP_TO_PREVIOUS = 5
const val ACTION_START = 6
const val ACTION_SEEK_TO = 7

class MusicPlayerService : Service() {
    private val mediaPlayer = MediaPlayer()
    private val timer = Timer()
    private val settings by lazy { Settings(application) }
    private val song: Song get() = CurrentPlayer.song.value!!
    private val imageDefault: Bitmap by lazy { BitmapFactory.decodeResource(resources, R.drawable.avatar) }
    private val mediaSessionCompat: MediaSessionCompat by lazy {
        MediaSessionCompat(this, "tag").apply {
            setCallback(mediaSessionCallback)
        }
    }
    private val mediaSessionCallback = object : MediaSessionCompat.Callback() {
        override fun onPause() {
            super.onPause()
            CurrentPlayer.pause()
        }

        override fun onPlay() {
            super.onPlay()
            CurrentPlayer.play()
            mediaSessionCompat.isActive = true
        }

        override fun onStop() {
            super.onStop()
            CurrentPlayer.clear()
            mediaSessionCompat.isActive = false
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
    }

    private val mediaMetadata
        get() = MediaMetadata.Builder().apply {
            putLong(MediaMetadata.METADATA_KEY_DURATION, /*song.duration.toLong()*/ -1L)
            putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, song.liveAlbumArt.value)
            putString(MediaMetadata.METADATA_KEY_TITLE, song.title)
            putString(MediaMetadata.METADATA_KEY_ARTIST, song.artist)
            putString(MediaMetadata.METADATA_KEY_ALBUM, song.album)
        }.build()

    private lateinit var audioManager: AudioManager
    private lateinit var audioAttributes: AudioAttributes
    private lateinit var focusRequest: AudioFocusRequest
    private var audioFocusRequest: Int = 0

    private val audioFocusChangeListener = OnAudioFocusChangeListener { focusChange ->
        val action = when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> ACTION_PLAY
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> ACTION_PAUSE
            AudioManager.AUDIOFOCUS_LOSS -> ACTION_PAUSE
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> ACTION_PAUSE
            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE -> ACTION_PLAY
            else -> -10
        }
        handleAction(action)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        mediaPlayer.setOnCompletionListener { CurrentPlayer.next() }
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
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
        intent ?: return START_NOT_STICKY
        log("onStartCommand")

        val action = intent.getIntExtra("action", 0)
        if(action == ACTION_SEEK_TO){
            val progress = intent.getIntExtra("progress", 0)
            mediaPlayer.seekTo(progress)
        }

        handleAction(action)
        MediaButtonReceiver.handleIntent(mediaSessionCompat, intent)

        return START_NOT_STICKY
    }

    private fun handleAction(action: Int) {
        when (action) {
            ACTION_PAUSE -> CurrentPlayer.pause()
            ACTION_PLAY -> CurrentPlayer.play()
            ACTION_CLEAR -> CurrentPlayer.clear()
            ACTION_SKIP_TO_PREVIOUS -> CurrentPlayer.previous()
            ACTION_SKIP_TO_NEXT -> CurrentPlayer.next()
            ACTION_START -> CurrentPlayer.start()
        }
    }

    private fun startMediaPlayer() {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(applicationContext, song.uri)
        mediaPlayer.prepare()
        mediaPlayer.setOnPreparedListener {
            it.startMediaPlayer()
            sendNotification()
            CurrentPlayer.duration.postValue(it.duration)
        }
    }

    private fun pauseMusic() {
        if (!mediaPlayer.isPlaying) return
        mediaPlayer.pause()
        sendNotification()
    }

    private fun playMusic() {
        if (mediaPlayer.isPlaying) return
        mediaPlayer.startMediaPlayer()
        sendNotification()
    }

    private fun MediaPlayer.startMediaPlayer() {
        if(settings.autoPause)
            audioFocusRequest = audioManager.requestAudioFocus(focusRequest)
        else
            audioManager.abandonAudioFocusRequest(focusRequest)
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
        val volume = it.div(100F)
        mediaPlayer.setVolume(volume, volume)
    }

    private val observerPlaying = Observer<Boolean> {
        if (it) playMusic()
        else pauseMusic()
    }

    private val observerSong = Observer<Song?> {
        it?.let { startMediaPlayer() } ?: stopSelf()
    }

    private fun registerLivedata() {
        CurrentPlayer.volume.observeForever(observerVolume)
        CurrentPlayer.isPlaying.observeForever(observerPlaying)
        CurrentPlayer.song.observeForever(observerSong)
    }

    private fun unRegisterLivedata() {
        CurrentPlayer.volume.removeObserver(observerVolume)
        CurrentPlayer.isPlaying.removeObserver(observerPlaying)
        CurrentPlayer.song.removeObserver(observerSong)
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
        val intent = Intent(this, MainActivity::class.java)
        intent.action = MainActivity.ACTION_EXPAND
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
                or PlaybackStateCompat.ACTION_STOP
            )
        }.build()

        mediaSessionCompat.apply {
            setMetadata(MediaMetadataCompat.fromMediaMetadata(mediaMetadata))
            setPlaybackState(playbackState)
        }

        val style = MediaStyle()
            .setShowActionsInCompactView(1, 3)
            .setMediaSession(mediaSessionCompat.sessionToken)

        val notification = NotificationCompat.Builder(this, CHANNEL_MEDIA_PLAYER).apply {
            setSmallIcon(R.drawable.music_note)
            setContentTitle(song.title)
            setContentText(if (song.artist == "") getString(R.string.unknown) else song.artist)
            setLargeIcon(song.largeAlbumArt ?: imageDefault)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setContentIntent(pendingIntent)
            addAction(R.drawable.previous, "previous", getPendingIntent(ACTION_SKIP_TO_PREVIOUS))
            if (mediaPlayer.isPlaying)
                addAction(R.drawable.pause, "pause", getPendingIntent(ACTION_PAUSE))
            else
                addAction(R.drawable.play, "resume", getPendingIntent(ACTION_PLAY))
            addAction(R.drawable.next, "next", getPendingIntent(ACTION_SKIP_TO_NEXT))
            addAction(R.drawable.clear, "clear", getPendingIntent(ACTION_CLEAR))
            setStyle(style)
        }.build()

        startForeground(1, notification)
    }

    //Service -> Broadcast -> Service
    private fun getPendingIntent(action: Int): PendingIntent {
        val intent = Intent(this, MyReceiver::class.java).apply {
            putExtra("action", action)
        }
        return PendingIntent.getBroadcast(
            applicationContext,
            action,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }
}