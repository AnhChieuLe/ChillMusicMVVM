package com.example.chillmusic.data

import android.app.Application
import android.content.ContentUris
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.util.Size
import com.example.chillmusic.R
import com.example.chillmusic.model.Album
import com.example.chillmusic.model.Artist
import com.example.chillmusic.model.Song
import kotlinx.coroutines.*

object MediaStoreManager {
    val songs: MutableSet<Song> = mutableSetOf()
    var albums: MutableSet<Album> = mutableSetOf()
    var artists: MutableSet<Artist> = mutableSetOf()

    @OptIn(DelicateCoroutinesApi::class)
    fun loadSong(application: Application) {
        val resolver = application.contentResolver

        val projection = arrayOf(
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.SIZE,
        )

        val collection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} ASC"

        val query = resolver.query(collection, projection, null, null, sortOrder) ?: return

        query.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val artistIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val path = cursor.getString(pathColumn) ?: continue
                val title = cursor.getString(titleColumn) ?: continue
                val artist = cursor.getString(artistColumn) ?: ""
                val artistId = cursor.getLong(artistIdColumn)
                val albumName = cursor.getString(albumColumn) ?: ""
                val albumId = cursor.getLong(albumIdColumn)
                val duration = cursor.getInt(durationColumn)
                if(duration < 60000)    continue
                val date = cursor.getLong(dateColumn)

                val song = Song(
                    id = id,
                    path = path,
                    title = title,
                    artist = artist,
                    artistId = artistId,
                    album = albumName,
                    albumId = albumId,
                    duration = duration,
                    date = date,
                    bitrate = 0L,
                )
                val handler = CoroutineExceptionHandler { _, exception ->
                    exception.printStackTrace()
                }
                GlobalScope.launch(Dispatchers.IO + handler) {
                    song.loadAlbumArt(resolver)
                }
                songs.add(song)
            }
        }
    }

    fun loadAlbum(){
        albums = songs.groupBy { it.albumId }.map {
            Album(
                id = it.key,
                name = it.value[0].album,
                ids = it.value.map { song -> song.id },
                albumArt = it.value[0].liveAlbumArt
            )
        }.toMutableSet()
    }

    fun loadArtist(){
        artists = songs.groupBy { it.artistId }.map {
            Artist(
                id = it.key,
                name = it.value[0].artist,
                ids = it.value.map { song -> song.id }
            )
        }.toMutableSet()
    }

    fun getSongs(vararg ids: Long) : List<Song>{
        val list = mutableListOf<Song>()
        ids.forEach { id ->
            songs.forEach { song ->
                if(song.id == id){
                    list += song
                }
            }
        }
        return list.toList()
    }
}