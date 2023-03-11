package com.example.chillmusic.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.example.chillmusic.model.Song

class SongsViewModel : ViewModel() {
    val songs : ObservableField<List<Song>> = ObservableField()
}