package com.example.chillmusic.model

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import androidx.palette.graphics.Palette
import androidx.palette.graphics.Palette.Swatch
import kotlin.math.min

class MusicStyle(val bitmap: Bitmap? = null) {
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

    init {
        bitmap?.let {
            var palette = Palette.Builder(bitmap).generate()
            if(palette.swatches.size < 2)
                palette = Palette.from(it).clearFilters().generate()

            palette.setColor1()
            //palette.setColor2()
        }
    }

    private fun Palette.setColor1() {
//        val muted = listOfNotNull(lightMutedSwatch, mutedSwatch, darkMutedSwatch)
//        if(generateColor(muted))    return
//
//        val vibrant = listOfNotNull(lightVibrantSwatch, vibrantSwatch, darkVibrantSwatch)
//        if(generateColor(vibrant))  return

        if(generateColor(swatches)) return
    }

    private fun Palette.setColor2(): Boolean{
        val swatch = dominantSwatch ?: vibrantSwatch ?: mutedSwatch ?: return false

        backgroundColor = swatch.population
        contentColor = swatch.rgb
        titleColor = swatch.titleTextColor
        bodyTextColor = swatch.bodyTextColor

        return true
    }

    private fun Palette.setColor3(){
    }

    private fun generateColor(swatches: List<Swatch>): Boolean {
        if (swatches.size < 2) return false

        val min = swatches.minBy { it.rgb }
        val max = swatches.maxBy { it.rgb }

        backgroundColor = min.rgb
        contentColor = max.rgb
        bodyTextColor = min.bodyTextColor
        titleColor = min.titleTextColor
        return true
    }

    private fun Int.getColorWithAlpha(alpha: Int): Int {
        return Color.argb(alpha, Color.red(this), Color.green(this), Color.blue(this))
    }
}