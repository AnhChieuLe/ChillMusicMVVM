package com.example.chillmusic.viewmodel

import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.chillmusic.R
import com.example.chillmusic.contant.log
import com.example.chillmusic.enums.Navigation
import com.example.chillmusic.model.MusicStyle
import com.example.chillmusic.model.Song
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
    val style       =   ObservableField(MusicStyle())
    val navigation  =   CurrentPlayer.navigation
    var isTouching  =   CurrentPlayer.isTouching

    private val songObserver = Observer<Song>{
        style.set(MusicStyle(it.smallImage))
    }

    init {
        song.observeForever(songObserver)
    }

    override fun onCleared() {
        super.onCleared()
        song.removeObserver(songObserver)
    }

    fun changeNavigation(view: View){
        navigation.postValue(when(navigation.value){
            Navigation.NORMAL -> Navigation.REPEAT
            Navigation.REPEAT -> Navigation.REPEAT_ONE
            Navigation.REPEAT_ONE -> Navigation.RANDOM
            Navigation.RANDOM -> Navigation.NORMAL
            else -> Navigation.NORMAL
        })
    }

    fun next(view: View){
        CurrentPlayer.next()
    }

    fun previous(view: View){
        CurrentPlayer.previous()
    }

    fun clear(view: View){
        CurrentPlayer.clear()
    }

    companion object {
        @BindingAdapter("android:color")
        @JvmStatic
        fun setColorStateList(view: BottomNavigationView, style: MusicStyle){
            view.setBackgroundColor(style.backgroundColor)
            view.itemIconTintList = style.stateList
            view.itemTextColor = style.stateList
        }

        @BindingAdapter("android:src")
        @JvmStatic
        fun setNavigationResource(view: ImageView, navigation: Navigation){
            view.setImageResource(when(navigation) {
                Navigation.NORMAL -> R.drawable.change
                Navigation.REPEAT -> R.drawable.repeat
                Navigation.REPEAT_ONE -> R.drawable.repeat_one
                Navigation.RANDOM -> R.drawable.random
            })
        }

        @BindingAdapter("android:duration_text")
        @JvmStatic
        fun setDurationText(view: TextView, duration: Int){
            view.text = getStringDuration(duration.toLong())
        }

        @BindingAdapter("android:style")
        @JvmStatic
        fun setSeekBarStyle(seekbar: SeekBar, style: MusicStyle){
            seekbar.progressDrawable.setTint(style.contentColor)
            seekbar.thumb.setTint(style.contentColor)
        }

        private fun getStringDuration(millisecond: Long): String {
            with(TimeUnit.MILLISECONDS){
                val hh = toHours(millisecond)
                val mm = toMinutes(millisecond) % 60
                val ss = toSeconds(millisecond) % 60

                return if (hh > 0)
                    String.format("%02d:%02d:%02d", hh, mm, ss)
                else
                    String.format("%02d:%02d", mm, ss)
            }
        }
    }
}