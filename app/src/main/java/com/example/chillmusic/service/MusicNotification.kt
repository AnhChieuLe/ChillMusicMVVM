package com.example.chillmusic.service

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadata
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import com.example.chillmusic.CHANNEL_MEDIA_PLAYER
import com.example.chillmusic.R
import com.example.chillmusic.activity.MainActivity
import com.example.chillmusic.model.Song
import com.example.chillmusic.receiver.MyReceiver
import java.time.Duration

class MusicNotification(
    private val context: Context,
    private var song: Song,
    private val mediaSession: MediaSessionCompat,
    private val isPlaying: Boolean,
    private val duration: Long
) {
    private val imageDefault: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.avatar)

    private val pendingIntent: PendingIntent get() {
        val intent = Intent(context, MainActivity::class.java)
        intent.action = MainActivity.ACTION_EXPAND
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }
    private val style
        get() = androidx.media.app.NotificationCompat.MediaStyle()
            .setShowActionsInCompactView(1, 3)
            .setMediaSession(mediaSession.sessionToken)

    private val mediaMetadata
        get() = MediaMetadata.Builder().apply {
            putLong(MediaMetadata.METADATA_KEY_DURATION, /*song.duration.toLong()*/ -1L)
            putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, song.liveAlbumArt.value ?: imageDefault)
            putString(MediaMetadata.METADATA_KEY_TITLE, song.title)
            putString(MediaMetadata.METADATA_KEY_ARTIST, song.artist)
            putString(MediaMetadata.METADATA_KEY_ALBUM, song.album)
        }.build()

    private val playbackState: PlaybackStateCompat
        get() {
            return PlaybackStateCompat.Builder().apply {
                setState(PlaybackStateCompat.STATE_PLAYING, duration, 1F)
                setActions(
                    PlaybackStateCompat.ACTION_SEEK_TO
                            or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                            or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                            or PlaybackStateCompat.ACTION_PAUSE
                            or PlaybackStateCompat.ACTION_PLAY
                            or PlaybackStateCompat.ACTION_STOP
                )
            }.build()
        }

    val notification: Notification
        get(){
            mediaSession.apply {
                setMetadata(MediaMetadataCompat.fromMediaMetadata(mediaMetadata))
                setPlaybackState(playbackState)
            }

            return NotificationCompat.Builder(context, CHANNEL_MEDIA_PLAYER).apply {
                setSmallIcon(R.drawable.music_note)
                setContentTitle(song.title)
                setContentText(if (song.artist == "") context.getString(R.string.unknown) else song.artist)
                setLargeIcon(song.largeAlbumArt ?: imageDefault)
                setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                setContentIntent(pendingIntent)
                addAction(R.drawable.previous, "previous", getPendingIntent(ACTION_SKIP_TO_PREVIOUS))
                if (isPlaying)
                    addAction(R.drawable.pause, "pause", getPendingIntent(ACTION_PAUSE))
                else
                    addAction(R.drawable.play, "resume", getPendingIntent(ACTION_PLAY))
                addAction(R.drawable.next, "next", getPendingIntent(ACTION_SKIP_TO_NEXT))
                addAction(R.drawable.clear, "clear", getPendingIntent(ACTION_CLEAR))
                setStyle(style)
            }.build()
        }

    //Service -> Broadcast -> Service
    private fun getPendingIntent(action: Int): PendingIntent {
        val intent = Intent(context, MyReceiver::class.java)
        intent.putExtra("action", action)
        return PendingIntent.getBroadcast(
            context,
            action,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }
}