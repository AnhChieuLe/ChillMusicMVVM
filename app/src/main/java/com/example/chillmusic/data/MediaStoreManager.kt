package com.example.chillmusic.data

import android.app.Application
import android.content.ContentUris
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.provider.MediaStore
import android.util.Size
import com.example.chillmusic.R
import com.example.chillmusic.contant.log
import com.example.chillmusic.model.Song
import java.io.IOException
import java.util.concurrent.TimeUnit

object MediaStoreManager {
    val songs = mutableSetOf<Song>()

    fun scanMusic(application: Application) {
        val imageSize = Size(128, 128)
        val defaultImage = BitmapFactory.decodeResource(application.resources, R.drawable.avatar)
        val resolver = application.contentResolver

        val projection = arrayOf(
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.SIZE,
//            MediaStore.Audio.Media.COMPOSER,
//            MediaStore.Audio.Media.TRACK,
//            MediaStore.Audio.Media.BITRATE,
//            MediaStore.Audio.Media.WRITER,
//            MediaStore.Audio.Media.GENRE
        )

        val collection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} ASC"
        val selection = "${MediaStore.Video.Media.DURATION} >= ?}"
        val selectionArgs = arrayOf("60000")

        val query = resolver.query(
            collection,
            projection,
            null,
            null,
            sortOrder
        ) ?: return

        query.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
//            val composerColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.COMPOSER)
//            val trackColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
//            val writerColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.WRITER)
//            val genreColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.GENRE)
//            val bitrate = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.BITRATE)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val path = cursor.getString(pathColumn) ?: continue
                val title = cursor.getString(titleColumn) ?: continue
                val artist = cursor.getString(artistColumn) ?: ""
                val album = cursor.getString(albumColumn) ?: ""
                val duration = cursor.getInt(durationColumn)
                val date = cursor.getLong(dateColumn)
                val size = cursor.getLong(sizeColumn)
//                val composer = cursor.getString(composerColumn)
//                val writer = cursor.getString(writerColumn)
//                val tracks = cursor.getInt(trackColumn)
//                val genre = cursor.getString(genreColumn)
//                val bitrate = cursor.getLong(bitrateColumn)
                val uri = ContentUris.withAppendedId(collection, id)

                val albumArt = try {
                    resolver.loadThumbnail(uri, imageSize, null)
                } catch (e: IOException) {
                    e.printStackTrace()
                    defaultImage
                }

                val song = Song(
                    id = id,
                    path = path,
                    title = title,
                    artist = artist,
                    album = album,
                    duration = duration,
                    date = date,
                    bitrate = 0L,
                    albumArt = albumArt,
//                    size = size,
//                    composer = composer,
//                    tracks = tracks
                )

                songs.add(song)
            }
        }
    }

    fun getSongs(vararg ids: Long) = songs.filter { it.id in ids }
}