package com.example.chillmusic.data

import android.app.Application
import android.media.MediaScannerConnection
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import com.example.chillmusic.enums.Sort
import com.example.chillmusic.enums.SortType
import com.example.chillmusic.model.Album
import com.example.chillmusic.model.Artist
import com.example.chillmusic.model.Song
import kotlinx.coroutines.*
import java.util.SortedSet

object MediaStoreManager {
    var sortType = SortType.DECREASING
    var sort = Sort.BY_DATE
    val allSong = MutableLiveData<SortedSet<Song>>(sortedSetOf(Comparator { song2, song1 -> (song1.id - song2.id).toInt() }))
    val songs get() = allSong.value!!
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

            val list: SortedSet<Song> = sortedSetOf(Comparator { song2, song1 -> (song1.id - song2.id).toInt() })

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
                list.add(song)
            }
            allSong.postValue(list)
            changeSort()
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

    fun refreshMediaStore(application: Application, callback: () -> Unit) {
        val paths = arrayOf("/storage/emulated/0/")
        MediaScannerConnection.scanFile(application, paths, null) { path, uri ->
            callback()
        }
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

    fun getSongs(vararg ids: Long) : MutableSet<Song>{
        val list = mutableSetOf<Song>()
        songs.forEach {
            if(ids.contains(it.id)){
                list += it
            }
        }
        return list
    }

    fun getSongs(id: Long) : Song?{
        return songs.firstOrNull { it.id == id }
    }

    fun setSortedValue(sort: Sort){
        if(this.sort == sort)   return
        this.sort = sort
        changeSort()
    }

    fun setSortedType(sortType: SortType){
        if(this.sortType == sortType)   return
        this.sortType = sortType
        changeSort()
    }

    private fun changeSort(){
        if(sortType == SortType.INCREASING)
            when(this.sort){
                Sort.BY_DATE -> setComparator { song1, song2 -> (song1.id - song2.id).toInt() }
                Sort.BY_NAME -> setComparator { song1, song2 -> song1.title.compareTo(song2.title) }
                Sort.BY_SIZE -> setComparator { song1, song2 -> (song1.size - song2.size).toInt() }
                Sort.BY_DURATION -> setComparator { song1, song2 ->  (song1.duration - song2.duration)}
                Sort.BY_QUALITY -> setComparator { song1, song2 -> (song1.bitrate - song2.bitrate).toInt() }
            }
        else
            when(this.sort){
                Sort.BY_DATE -> setComparator { song2, song1 -> (song1.id - song2.id).toInt() }
                Sort.BY_NAME -> setComparator { song2, song1 -> song1.title.compareTo(song2.title) }
                Sort.BY_SIZE -> setComparator { song2, song1 -> (song1.size - song2.size).toInt() }
                Sort.BY_DURATION -> setComparator { song2, song1 ->  (song1.duration - song2.duration)}
                Sort.BY_QUALITY -> setComparator { song2, song1 -> (song1.bitrate - song2.bitrate).toInt() }
            }
    }

    private fun setComparator(comparator: (Song, Song) -> Int){
        allSong.setComparator(Comparator(comparator))
    }

    operator fun MutableLiveData<SortedSet<Song>>.plusAssign(values: Song) {
        val value = this.value ?: sortedSetOf()
        value.add(values)
        this.postValue(value)
    }

    fun MutableLiveData<SortedSet<Song>>.setComparator(compare: Comparator<Song>) {
        val value = this.value ?: sortedSetOf(compare)
        this.postValue(value.toSortedSet(compare))
    }
}