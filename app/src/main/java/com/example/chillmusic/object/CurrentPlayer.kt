package com.example.chillmusic.`object`

import android.content.res.ColorStateList
import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.example.chillmusic.R
import com.example.chillmusic.enums.Navigation
import com.example.chillmusic.model.MusicStyle
import com.example.chillmusic.model.PlayList
import com.example.chillmusic.model.Song
import com.example.chillmusic.`object`.CurrentPlayer.mapWithDefault
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.concurrent.TimeUnit

object CurrentPlayer : ViewModel() {
    var playList = PlayList()
    val name = MutableLiveData("")
    var song = MutableLiveData<Song>()
    val isPlaying = MutableLiveData(false)
    val isActive = MutableLiveData(false)
    val volume = MutableLiveData(50)
    val progress = MutableLiveData(0)
    val duration = MutableLiveData(0)
    val navigation = MutableLiveData(Navigation.NORMAL)
    val isTouching = MutableLiveData(false)
    val liveStyle = song.mapWithDefault(MusicStyle()) {
        MusicStyle(it.smallImage)
    }
    val style = ObservableField(MusicStyle())

    private fun LiveData<Song>.mapWithDefault(defaultValue: MusicStyle, mapper: (Song) -> MusicStyle) : LiveData<MusicStyle>{
        return MediatorLiveData<MusicStyle>().apply {
            postValue(defaultValue)
            addSource(this@mapWithDefault) {
                postValue(mapper(it))
                style.set(MusicStyle(it.smallImage))
            }
        }
    }

    fun start() {
        isActive.postValue(true)
        isPlaying.postValue(true)
    }

    fun next() {
        song.postValue(playList.getNext(song.value, navigation.value!!))
    }

    fun previous() {
        song.postValue(playList.previous(song.value, navigation.value!!))
    }

    fun pause() {
        isPlaying.postValue(false)
    }

    fun resume() {
        isPlaying.postValue(true)
    }

    fun clear() {
        isActive.postValue(false)
    }

    fun changeNavigation() {
        navigation.postValue(
            when (navigation.value) {
                Navigation.NORMAL -> Navigation.REPEAT
                Navigation.REPEAT -> Navigation.REPEAT_ONE
                Navigation.REPEAT_ONE -> Navigation.RANDOM
                Navigation.RANDOM -> Navigation.NORMAL
                else -> Navigation.NORMAL
            }
        )
    }

    @BindingAdapter("android:color")
    @JvmStatic
    fun setColorStateList(view: BottomNavigationView, style: MusicStyle?){
        if(style == null)   return
        view.setBackgroundColor(style.backgroundColor)
        view.itemIconTintList = style.stateList
        view.itemTextColor = style.stateList
    }

    @BindingAdapter("android:tint")
    @JvmStatic
    fun setFloatingActionButtonStyle(fab: FloatingActionButton, style: MusicStyle){
        fab.imageTintList = ColorStateList.valueOf(style.contentColor)
        fab.backgroundTintList = ColorStateList.valueOf(style.titleColor)
    }

    @BindingAdapter("android:tint")
    @JvmStatic
    fun setFloatingActionButtonStyle(button: RadioButton, style: MusicStyle){
        if(button.isChecked)
            button.buttonTintList = ColorStateList.valueOf(style.contentColor)
        else
            button.buttonTintList = ColorStateList.valueOf(style.bodyTextColor)
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