package com.example.chillmusic.viewmodel

import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.chillmusic.R
import com.example.chillmusic.contant.log
import com.example.chillmusic.enums.Navigation
import com.example.chillmusic.model.MusicStyle
import com.example.chillmusic.`object`.CurrentPlayer
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.concurrent.TimeUnit

class CurrentPlayerViewModel : ViewModel() {
    var song        =   CurrentPlayer.song
    val isPlaying   =   CurrentPlayer.isPlaying
    val volume      =   CurrentPlayer.volume
    val progress    =   CurrentPlayer.progress
    val duration    =   CurrentPlayer.duration
    val isActive    =   CurrentPlayer.isActive
    val navigation  =   CurrentPlayer.navigation
    var isTouching  =   CurrentPlayer.isTouching
    val style       =   song.map {
        MusicStyle(it.smallImage)
    }

    fun changeNavigation(){
        navigation.postValue(when(navigation.value){
            Navigation.NORMAL -> Navigation.REPEAT
            Navigation.REPEAT -> Navigation.REPEAT_ONE
            Navigation.REPEAT_ONE -> Navigation.RANDOM
            Navigation.RANDOM -> Navigation.NORMAL
            else -> Navigation.NORMAL
        })
    }

    fun next(){
        CurrentPlayer.next()
    }

    fun previous(){
        CurrentPlayer.previous()
    }

    fun clear(){
        CurrentPlayer.clear()
    }

    companion object {

    }
}