package com.example.chillmusic.model

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import kotlinx.parcelize.Parcelize

@Parcelize
data class Song(
    var id: Int = 0,
    var path: String = "",
    var contentUri: Uri,
    var title: String = "",
    var artist: String = "",
    var date: Long = 0,
    var duration: Int = 0,
    var genre: String = "",
    var album: String = "",
    var bitrate: Long = 0,
    var smallImage: Bitmap,
) : Parcelable{
    companion object {
        @BindingAdapter("android:bitmap")
        @JvmStatic
        fun setImage(view : ImageView, bitmap: Bitmap?){
            bitmap?.let{
                view.setImageBitmap(it)
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        return (other as Song?)?.id == this.id
    }
}
