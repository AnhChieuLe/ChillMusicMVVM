package com.example.chillmusic.viewmodel

import android.graphics.Bitmap
import android.graphics.Paint
import android.view.View
import android.widget.*
import androidx.databinding.BindingAdapter
import com.example.chillmusic.R
import com.example.chillmusic.enums.Navigation
import com.example.chillmusic.library.MusicStyle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.pow
import kotlin.math.roundToInt


@BindingAdapter("android:track")
fun setText(textView: TextView, content: Int?) {
    content ?: return
    textView.text = content.toString()
}

@BindingAdapter("android:text")
fun setText(textView: TextView, content: String?) {
    if(content == null || content == "" || content == "<unknown>"){
        textView.setText(R.string.unknown)
    }else{
        textView.text = content
    }
}

@BindingAdapter("android:fileSize")
fun setFileSize(textView: TextView, content: Long?) {
    content ?: return
    val str = (content / 2.0.pow(20.0)).roundToInt().toString() + " MB"
    textView.text = str
}

@BindingAdapter("android:bitrate")
fun setBitrate(textView: TextView, content: Long?) {
    content ?: return
    var str = (content / 1024).toString()
    str += " kbps"
    textView.text = str
}

@BindingAdapter("android:date")
fun setDate(textView: TextView, date: Long?) {
    date ?: return
    val simpleDateFormat = SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault())
    textView.text = simpleDateFormat.format(Date(date))
}

@BindingAdapter("android:duration_text")
fun setDurationText(textView: TextView, duration: Int?) {
    duration ?: return
    textView.text = getStringDuration(duration.toLong())
}

@BindingAdapter("android:duration_text")
fun setDurationText(textView: TextView, duration: Long?) {
    duration ?: return
    textView.text = getStringDuration(duration.toLong())
}

@BindingAdapter("android:src")
fun setNavigationResource(imageView: ImageView, navigation: Navigation?) {
    navigation ?: return
    imageView.setImageResource(
        when (navigation) {
            Navigation.NORMAL -> R.drawable.change
            Navigation.REPEAT -> R.drawable.repeat
            Navigation.REPEAT_ONE -> R.drawable.repeat_one
            Navigation.RANDOM -> R.drawable.random
        }
    )
}

@BindingAdapter("android:color")
fun setColorStateList(bottomNavigationView: BottomNavigationView, style: MusicStyle?) {
    style ?: return
    bottomNavigationView.setBackgroundColor(style.backgroundColor)
    bottomNavigationView.itemIconTintList = style.stateList
    bottomNavigationView.itemTextColor = style.stateList
}

@BindingAdapter("android:color")
fun setTabLayoutColor(tabLayout: TabLayout, style: MusicStyle?) {
    style ?: return
    tabLayout.setTabTextColors(style.titleColor, style.contentColor)
    tabLayout.setSelectedTabIndicatorColor(style.contentColor)
}

@BindingAdapter("android:bitmap")
fun setImage(imageView: ImageView, bitmap: Bitmap?) {
    if(bitmap != null)
        imageView.setImageBitmap(bitmap)
    else
        imageView.setImageResource(R.drawable.avatar)
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

@BindingAdapter("android:style")
fun setNavigationStyle(navigation: NavigationView, style: MusicStyle?){
    style ?: return
    navigation.itemIconTintList = style.stateList
    navigation.setBackgroundColor(style.backgroundColor)
    navigation.itemTextColor = style.stateList
}

@BindingAdapter("android:style")
fun setNumberPickerStyle(numberPicker: NumberPicker, style: MusicStyle?){
    style ?: return
    numberPicker.textColor = style.contentColor

    numberPicker.textSize = 64F
    numberPicker.textSize = 64F
    numberPicker.textSize = 64F

    numberPicker.selectionDividerHeight = 0
    numberPicker.selectionDividerHeight = 0
    numberPicker.selectionDividerHeight = 0
}

@BindingAdapter("android:max")
fun setNumberPickerValue(numberPicker: NumberPicker, max: Int?){
    max ?: return
    numberPicker.minValue = 0
    numberPicker.maxValue = max
}

private fun getStringDuration(millisecond: Long?): String {
    millisecond ?: return ""
    if(millisecond < 0) return ""

    val hh = TimeUnit.MILLISECONDS.toHours(millisecond)
    val mm = TimeUnit.MILLISECONDS.toMinutes(millisecond) % 60
    val ss = TimeUnit.MILLISECONDS.toSeconds(millisecond) % 60

    return if (hh > 0)
        String.format("%02d:%02d:%02d", hh, mm, ss)
    else
        String.format("%02d:%02d", mm, ss)
}