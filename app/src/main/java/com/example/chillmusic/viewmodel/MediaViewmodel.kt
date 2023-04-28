package com.example.chillmusic.repository

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.chillmusic.constant.log
import com.example.chillmusic.data.MediaStoreManager
import com.example.chillmusic.data.blacklist.BlackList
import com.example.chillmusic.data.blacklist.BlackListDatabase
import com.example.chillmusic.data.favorite.Favorite
import com.example.chillmusic.data.favorite.FavoriteDatabase
import com.example.chillmusic.enums.Sort
import com.example.chillmusic.enums.SortType
import com.example.chillmusic.library.SongComparator
import com.example.chillmusic.model.Album
import com.example.chillmusic.model.Artist
import com.example.chillmusic.model.Song
import com.example.chillmusic.viewmodel.CurrentPlayer
import java.util.SortedSet

class MediaViewModel(application: Application) : AndroidViewModel(application) {
    val songs: MediatorLiveData<SortedSet<Song>> = MediatorLiveData()

    val allMedia: List<Song>
        get() = songs.value?.toList() ?: listOf()

    private val filterBlackList: List<Song>
        get() = songs.value?.filterNot { it.isBlackList }?.toList() ?: listOf()

    val longMedia: List<Song>
        get() = filterBlackList.filter { it.duration > 600000 }

    val shortMedia: List<Song>
        get() = filterBlackList.filter { it.duration <= 600000 }

    val favorites: List<Song>
        get() = filterBlackList.filter { it.isFavorite }

    val blackList: List<Song>
        get() = songs.value?.filter { it.isBlackList }?.toList() ?: listOf()

    val albums: List<Album>
        get() = filterBlackList.groupBy { it.albumId }.map {
            Album(
                id = it.key,
                name = it.value[0].album,
                ids = it.value.map { song -> song.id },
                albumArt = it.value[0].liveAlbumArt
            )
        }

    val artists: List<Artist>
        get() = filterBlackList.groupBy { it.artistId }.map {
            Artist(
                id = it.key,
                name = it.value[0].artist,
                ids = it.value.map { song -> song.id }
            )
        }
    private val comparator = SongComparator()
    private val blackListRepo: BlackListRepository
    private val favoriteRepo: FavoriteRepository

    init {
        val blackListDao = BlackListDatabase.getDatabase(application).blackListDao()
        val favoriteDao = FavoriteDatabase.getDatabase(application).favoriteDao()
        blackListRepo = BlackListRepository(blackListDao)
        favoriteRepo = FavoriteRepository(favoriteDao)

        songs.addSource(MediaStoreManager.allSong) {
            songs.postValue(it.toSortedSet(comparator))
            log("Songs changed, size: ${it.size}")
        }
        songs.addSource(blackListRepo.blackList) { bl ->
            val value = songs.value
            value?.forEach { song ->
                song.isBlackList = false
                bl.forEach { item ->
                    if (song.id == item.songId)
                        song.isBlackList = true
                }
            }
            songs.postValue(value ?: sortedSetOf())
            log("Blacklist changed, size: ${bl.size}")
        }
        songs.addSource(favoriteRepo.favorites) { fvs ->
            val value = songs.value
            value?.forEach { song ->
                song.isFavorite = false
                fvs.forEach { favorite ->
                    if (song.id == favorite.songId)
                        song.isFavorite = true
                }
            }
            songs.postValue(value ?: sortedSetOf())
            log("Favorite changed, size: ${fvs.size}")
        }
    }

    fun setSort(sort: Sort) {
        if (comparator.sort != sort) {
            comparator.sort = sort
            setComparator()
        }
    }

    fun setSortType(sortType: SortType) {
        if (comparator.sortType != sortType) {
            comparator.sortType = sortType
            setComparator()
        }
    }

    private fun setComparator() {
        songs.postValue(songs.value!!.toSortedSet(comparator))
    }
}