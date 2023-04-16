package com.example.chillmusic.activity

import android.Manifest
import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.example.chillmusic.R
import com.example.chillmusic.constant.log
import com.example.chillmusic.data.MediaStoreManager
import com.example.chillmusic.model.MusicStyle
import com.example.chillmusic.model.Song
import com.example.chillmusic.viewmodel.CurrentPlayer
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission


class FlashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CurrentPlayer.defaultStyle = MusicStyle(BitmapFactory.decodeResource(resources, R.drawable.avatar))
        requestPermission {
            refreshMediaStore {
                MediaStoreManager.loadSong(application)
                MediaStoreManager.loadAlbum()
                MediaStoreManager.loadArtist()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun refreshMediaStore(callback: () -> Unit) {
        val paths = arrayOf("/storage/emulated/0/")
        MediaScannerConnection.scanFile(this, paths, null) { path, uri ->
            callback()
        }
    }

    private fun deleteMedia(id: Long) {
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media._ID + "=?"
        val args = arrayOf(id.toString())
        //val num = contentResolver.delete(uri, selection, args)

        val contentUri = ContentUris.withAppendedId(uri, id)
        val num = contentResolver.delete(contentUri, null, null)
        log("Number of song deleted: $num")
    }

    private fun updateMetadata(song: Song) {
        val values = ContentValues().apply {
            put(MediaStore.Audio.Media.ALBUM, song.album)
            put(MediaStore.Audio.Media.ARTIST, song.artist)
            put(MediaStore.Audio.Media.TITLE, song.title)
            put(MediaStore.Audio.Media.DURATION, song.duration)
            put(MediaStore.Audio.Media.DATE_ADDED, song.date)
            put(MediaStore.Audio.Media.IS_MUSIC, true)
        }

        fun getPendingValues(is_pending: Boolean) = ContentValues().apply {
            put(MediaStore.Audio.Media.IS_PENDING, if (is_pending) 1 else 0)
        }

        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val contentUri = ContentUris.withAppendedId(uri, song.id)

        contentResolver.update(contentUri, getPendingValues(true), null, null)
        val rowsUpdated = contentResolver.update(contentUri, values, null, null)
        contentResolver.update(contentUri, getPendingValues(false), null, null)

        log("Number of song updated: $rowsUpdated")
    }

    private fun requestPermission(actionGranted: () -> Unit = {}) {
        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                actionGranted()
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                finish()
            }
        }

        TedPermission.create()
            .setDeniedMessage("Từ chối con cac")
            .setPermissionListener(permissionListener)
            .setPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .check()
    }
}