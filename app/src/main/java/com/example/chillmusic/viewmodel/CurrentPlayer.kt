package com.example.chillmusic.viewmodel

import android.graphics.Bitmap
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.*
import com.example.chillmusic.R
import com.example.chillmusic.model.PlayList
import com.example.chillmusic.enums.Navigation
import com.example.chillmusic.model.MusicStyle
import com.example.chillmusic.model.Song
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.pow
import kotlin.math.roundToInt

object CurrentPlayer : ViewModel() {
    var playList = PlayList()
    val name = MutableLiveData("")
    var song = MutableLiveData<Song?>(null)
    val isPlaying = MutableLiveData(false)
    val volume = MutableLiveData(50)
    val progress = MutableLiveData(0)
    val duration = MutableLiveData(0)
    val navigation = MutableLiveData(Navigation.NORMAL)
    val isTouching = MutableLiveData(false)
    val style = song.mapWithDefault(MusicStyle()) {
        MusicStyle(it?.largeAlbumArt ?: it?.albumArt)
    }

    private fun LiveData<Song?>.mapWithDefault(defaultValue: MusicStyle, mapper: (Song?) -> MusicStyle): LiveData<MusicStyle> {
        return MediatorLiveData<MusicStyle>().apply {
            postValue(defaultValue)
            addSource(this@mapWithDefault) {
                it?.let { postValue(mapper(it)) }
            }
        }
    }

    fun start() {
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
        song.postValue(null)
    }

    fun addToNext(id: Long){
        song.value?.let { playList.addToNext(it, id) }
    }

    fun addToLast(id: Long){
        playList.addToLast(id)
    }

    fun newPlayList(id: Long){
        playList.newPlayList(id)
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
    fun setColorStateList(view: BottomNavigationView, style: MusicStyle?) {
        if (style == null) return
        view.setBackgroundColor(style.backgroundColor)
        view.itemIconTintList = style.stateList
        view.itemTextColor = style.stateList
    }

    @BindingAdapter("android:text")
    @JvmStatic
    fun setText(textView: TextView, content: Int){
        textView.text = content.toString()
    }

    @BindingAdapter("android:fileSize")
    @JvmStatic
    fun setFileSize(textView: TextView, content: Long){
        val str = (content / 2.0.pow(20.0)).roundToInt().toString() + " MB"
        textView.text = str
    }

    @BindingAdapter("android:bitrate")
    @JvmStatic
    fun setBitrate(textView: TextView, content: Long){
        var str = (content / 1024).toString()
        str += " kbps"
        textView.text = str
    }

    @BindingAdapter("android:date")
    @JvmStatic
    fun setDate(textView: TextView, date: Long){
        val simpleDateFormat = SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault())
        textView.text = simpleDateFormat.format(Date(date))
    }

    @BindingAdapter("android:src")
    @JvmStatic
    fun setNavigationResource(view: ImageView, navigation: Navigation) {
        view.setImageResource(
            when (navigation) {
                Navigation.NORMAL -> R.drawable.change
                Navigation.REPEAT -> R.drawable.repeat
                Navigation.REPEAT_ONE -> R.drawable.repeat_one
                Navigation.RANDOM -> R.drawable.random
            }
        )
    }

    @BindingAdapter("android:duration_text")
    @JvmStatic
    fun setDurationText(view: TextView, duration: Int) {
        view.text = getStringDuration(duration.toLong())
    }

    @BindingAdapter("android:bitmap")
    @JvmStatic
    fun setImage(view : ImageView, bitmap: Bitmap?){
        bitmap?.let{
            view.setImageBitmap(it)
        } ?: view.setImageResource(R.drawable.avatar)
    }

    @BindingAdapter("android:style")
    @JvmStatic
    fun setSeekBarStyle(seekbar: SeekBar, style: MusicStyle) {
        seekbar.progressDrawable.setTint(style.contentColor)
        seekbar.thumb.setTint(style.contentColor)
    }

    @BindingAdapter("android:style")
    @JvmStatic
    fun setProgressBarStyle(progressBar: ProgressBar, style: MusicStyle?) {
        style?.let { progressBar.progressDrawable.setTint(it.contentColor) }
    }

    private fun getStringDuration(millisecond: Long): String {
        val hh = TimeUnit.MILLISECONDS.toHours(millisecond)
        val mm = TimeUnit.MILLISECONDS.toMinutes(millisecond) % 60
        val ss = TimeUnit.MILLISECONDS.toSeconds(millisecond) % 60

        return if (hh > 0)
            String.format("%02d:%02d:%02d", hh, mm, ss)
        else
            String.format("%02d:%02d", mm, ss)
    }
}