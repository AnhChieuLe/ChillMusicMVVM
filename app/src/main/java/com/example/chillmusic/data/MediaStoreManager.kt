package com.example.chillmusic.data

import android.app.Application
import android.content.ContentResolver
import android.media.MediaScannerConnection
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import com.example.chillmusic.data.blacklist.BlackList
import com.example.chillmusic.data.blacklist.BlackListDatabase
import com.example.chillmusic.data.favorite.Favorite
import com.example.chillmusic.data.favorite.FavoriteDatabase
import com.example.chillmusic.enums.Sort
import com.example.chillmusic.enums.SortType
import com.example.chillmusic.model.Album
import com.example.chillmusic.model.Artist
import com.example.chillmusic.model.Song
import com.example.chillmusic.repository.BlackListRepository
import com.example.chillmusic.repository.FavoriteRepository
import kotlinx.coroutines.*
import java.util.SortedSet

object MediaStoreManager {
    val allSong = MutableLiveData<Set<Song>>(setOf())
    val songs get() = allSong.value?.toList() ?: listOf()

    fun loadData(resolver: ContentResolver) {
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
        val scope = CoroutineScope(Dispatchers.Default)

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

            val list: MutableSet<Song> = mutableSetOf()

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val path = cursor.getString(pathColumn) ?: continue
                val title = cursor.getString(titleColumn) ?: continue
                val artist = cursor.getString(artistColumn) ?: ""
                val artistId = cursor.getLong(artistIdColumn)
                val albumName = cursor.getString(albumColumn) ?: ""
                val albumId = cursor.getLong(albumIdColumn)
                val duration = cursor.getInt(durationColumn)
                if (duration < 60000) continue
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
                scope.launch(Dispatchers.IO + handler) {
                    song.loadAlbumArt(resolver)
                }
                list.add(song)
            }

            allSong.postValue(list)
        }
    }

    fun refreshMediaStore(application: Application, callback: () -> Unit) {
        val paths = arrayOf("/storage/emulated/0/")
        MediaScannerConnection.scanFile(application, paths, null) { path, uri ->
            callback()
        }
    }

    fun getSongs(ids: List<Long>): List<Song> {
        val list = mutableListOf<Song>()
        ids.forEach { id ->
            songs.forEach { song ->
                if (id == song.id) {
                    list += song
                }
            }
        }
        return list
    }

    fun getSongs(id: Long): Song? {
        return songs.firstOrNull { it.id == id }
    }

    operator fun MutableLiveData<SortedSet<Song>>.plusAssign(values: Song) {
        val value = this.value ?: sortedSetOf()
        value.add(values)
        this.postValue(value)
    }
}