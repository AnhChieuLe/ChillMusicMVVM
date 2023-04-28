package com.example.chillmusic.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.chillmusic.R
import com.example.chillmusic.data.favorite.FavoriteDatabase
import com.example.chillmusic.model.PlayList
import com.example.chillmusic.data.playlist.PlayListDataBase
import com.example.chillmusic.repository.PlayListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlayListViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PlayListRepository
    val playLists: MediatorLiveData<List<PlayList>> = MediatorLiveData(listOf())

    init {
        val playlistDao = PlayListDataBase.getDatabase(application).playListDao()
        repository = PlayListRepository(playlistDao)
        playLists.addSource(repository.playLists) {
            playLists.value = it
        }

        val favoriteDao = FavoriteDatabase.getDatabase(application).favoriteDao()
        playLists.addSource(favoriteDao.getAll()) {
            val ids = it.map { favorite -> favorite.songId }.toMutableList()
            val name = application.resources.getString(R.string.favorite)

            val value = playLists.value?.toMutableSet() ?: mutableSetOf()
            value.removeIf { favorite -> favorite.name == name }
            value.add(PlayList(name, ids))
            playLists.value = value.toList()
        }
    }

    fun insert(playList: PlayList) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(playList)
        }
    }

    fun delete(playList: PlayList) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(playList)
        }
    }

    fun update(playList: PlayList) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(playList)
        }
    }
}