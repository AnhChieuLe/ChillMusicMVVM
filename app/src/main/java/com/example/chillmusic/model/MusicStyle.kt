package com.example.chillmusic.model

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.alpha
import androidx.palette.graphics.Palette

class MusicStyle() {
    var titleColor: Int = Color.GRAY
    var bodyTextColor: Int = Color.BLACK
    var backgroundColor: Int = Color.WHITE
    var contentColor: Int = Color.BLACK
    val itemBackGround get() = bodyTextColor.getColorWithAlpha(10)
    val stateList: ColorStateList
        get() {
            val states = arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf(-android.R.attr.state_checked)
            )
            val colors = intArrayOf(
                contentColor,
                titleColor
            )
            return ColorStateList(states, colors)
        }

    constructor(bitmap: Bitmap) : this() {
        val palette = Palette.from(bitmap).clearFilters().generate()
        val swatch1 = palette.darkMutedSwatch ?: palette.mutedSwatch ?: palette.lightMutedSwatch ?: palette.dominantSwatch

        val listAllMuted = ArrayList<Int>()
        val listAllVibrant = ArrayList<Int>()
        val listAllColor = ArrayList<Int>()

        with(palette) {
            darkVibrantSwatch?.rgb?.let { listAllVibrant.add(it) }
            vibrantSwatch?.rgb?.let { listAllVibrant.add(it) }
            lightVibrantSwatch?.rgb?.let { listAllVibrant.add(it) }
            darkMutedSwatch?.rgb?.let { listAllMuted.add(it) }
            mutedSwatch?.rgb?.let { listAllMuted.add(it) }
            lightMutedSwatch?.rgb?.let { listAllMuted.add(it) }
        }
        listAllColor.addAll(listAllMuted)
        listAllColor.addAll(listAllVibrant)

        titleColor = swatch1?.titleTextColor ?: 0
        bodyTextColor = swatch1?.bodyTextColor ?: 0

        backgroundColor = listAllMuted.min()
        contentColor = if (backgroundColor == listAllColor.max())
            listAllVibrant.max()
        else
            listAllColor.max()
    }

    fun Int.getColorWithAlpha(alpha: Int): Int{
        return Color.argb(alpha, Color.red(this), Color.green(this), Color.blue(this))
    }
}