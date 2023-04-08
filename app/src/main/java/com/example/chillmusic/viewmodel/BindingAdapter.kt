package com.example.chillmusic.viewmodel

import android.graphics.Bitmap
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.chillmusic.R
import com.example.chillmusic.enums.Navigation
import com.example.chillmusic.model.MusicStyle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.pow
import kotlin.math.roundToInt

@BindingAdapter("android:color")
fun setColorStateList(view: BottomNavigationView, style: MusicStyle?) {
    style ?: return
    view.setBackgroundColor(style.backgroundColor)
    view.itemIconTintList = style.stateList
    view.itemTextColor = style.stateList
}

@BindingAdapter("android:color")
fun setTabLayoutColor(tabLayout: TabLayout, style: MusicStyle?){
    style ?: return
    tabLayout.setTabTextColors(style.titleColor, style.contentColor)
    tabLayout.setSelectedTabIndicatorColor(style.contentColor)
}

@BindingAdapter("android:text")
fun setText(textView: TextView, content: Int?){
    content ?: return
    textView.text = content.toString()
}

@BindingAdapter("android:fileSize")
fun setFileSize(textView: TextView, content: Long?){
    content ?: return
    val str = (content / 2.0.pow(20.0)).roundToInt().toString() + " MB"
    textView.text = str
}

@BindingAdapter("android:bitrate")
fun setBitrate(textView: TextView, content: Long?){
    content ?: return
    var str = (content / 1024).toString()
    str += " kbps"
    textView.text = str
}

@BindingAdapter("android:date")
fun setDate(textView: TextView, date: Long?){
    date ?: return
    val simpleDateFormat = SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault())
    textView.text = simpleDateFormat.format(Date(date))
}

@BindingAdapter("android:src")
fun setNavigationResource(view: ImageView, navigation: Navigation?) {
    navigation ?: return
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
fun setDurationText(view: TextView, duration: Int?) {
    duration ?: return
    view.text = getStringDuration(duration.toLong())
}

@BindingAdapter("android:bitmap")
fun setImage(view : ImageView, bitmap: Bitmap?){
    bitmap?.let{
        view.setImageBitmap(it)
    } ?: view.setImageResource(R.drawable.avatar)
}

@BindingAdapter("android:style")
fun setSeekBarStyle(seekbar: SeekBar, style: MusicStyle?) {
    style ?: return
    seekbar.progressDrawable.setTint(style.contentColor)
    seekbar.thumb.setTint(style.contentColor)
}

@BindingAdapter("android:style")
fun setProgressBarStyle(progressBar: ProgressBar, style: MusicStyle?) {
    style ?: return
    progressBar.progressDrawable.setTint(style.contentColor)
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