package com.example.chillmusic.`object`

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.example.chillmusic.R
import com.example.chillmusic.model.Song
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import java.io.File
import kotlin.collections.ArrayList

object ListSongManager {
    var listSong = mutableListOf<Song>()
    fun setListAudio(context: Context) {
        val list: MutableList<Song> = ArrayList()
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media.DATA, //path
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM,
        )
        val oder = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"
        val query = context.contentResolver.query(uri, projection, null, null, oder)
        query?.use { cursor ->
            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)

            while (cursor.moveToNext()) {
                val path = cursor.getString(pathColumn) ?: continue
                val id = cursor.getInt(idColumn)

                val song = getSongWithMetadata(context, id, path)

                song?.let {
                    list.add(it)
                }
            }
            cursor.close()
        }
        list.sortByDescending { it.date }
        listSong = list
    }

    private fun getSongWithMetadata(context: Context, id: Int, path: String): Song? {
        val meta = MediaMetadataRetriever()
        try {
            meta.setDataSource(path)
        } catch (_: Exception) {
            return null
        }
        val contentUri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            id.toLong()
        )
        val duration = meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toInt() ?: 0
        if(duration < 60000)    return null
        val title = meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: File(path).nameWithoutExtension
        val artist = meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: ""
        val genre = meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE) ?: ""
        val album = meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) ?: ""
        val bitrate = meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)?.toLong() ?: 0
        val date = File(path).lastModified()

        val smallImage = if(meta.embeddedPicture == null)
            BitmapFactory.decodeResource(context.resources, R.drawable.avatar)
        else
            BitmapFactory.decodeByteArray(meta.embeddedPicture!!, 0, meta.embeddedPicture!!.size)

        return Song(
            id = id,
            path = path,
            contentUri = contentUri,
            title = title,
            artist = artist,
            date = date,
            duration = duration,
            genre = genre,
            album = album,
            bitrate = bitrate,
            smallImage = smallImage
        )
    }

    fun requestPermission(actionGranted: () -> Unit = {}, actionDenied: () -> Unit = {}) {
        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                actionGranted()
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                actionDenied()
            }
        }

        TedPermission.create()
            .setDeniedMessage("Từ chối con cac")
            .setPermissionListener(permissionListener)
            .setPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                //Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .check()
    }
}