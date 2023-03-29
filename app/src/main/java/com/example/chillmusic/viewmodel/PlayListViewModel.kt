package com.example.chillmusic.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.chillmusic.model.PlayList
import com.example.chillmusic.data.playlist.PlayListDataBase
import com.example.chillmusic.repository.PlayListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlayListViewModel(application: Application) : AndroidViewModel(application){
    private val repository: PlayListRepository
    val playLists: LiveData<List<PlayList>>
    init {
        val dao = PlayListDataBase.getDatabase(application).playListDao()
        repository = PlayListRepository(dao)
        playLists = repository.playLists
    }

    fun insert(playList: PlayList){
        viewModelScope.launch(Dispatchers.IO){
            repository.insert(playList)
        }
    }

    fun delete(playList: PlayList){
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(playList)
        }
    }

    fun update(playList: PlayList){
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(playList)
        }
    }
}