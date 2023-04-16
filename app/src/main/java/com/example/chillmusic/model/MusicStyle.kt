package com.example.chillmusic.model

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.ColorUtils
import androidx.palette.graphics.Palette

class MusicStyle(val bitmap: Bitmap? = null) {
    var titleColor: Int = Color.GRAY
    var bodyTextColor: Int = Color.BLACK
    var backgroundColor: Int = Color.WHITE
    var contentColor: Int = Color.BLACK

    val itemBackGround: Int get() = backgroundColor.getContainer()

    private val states = arrayOf(
        intArrayOf(android.R.attr.state_checked),
        intArrayOf(-android.R.attr.state_checked)
    )
    val stateList get() = ColorStateList(states, intArrayOf(contentColor, titleColor))

    init {
        bitmap?.let {
            var palette = Palette.Builder(bitmap).generate()
            if (palette.swatches.size < 2)
                palette = Palette.from(it).clearFilters().generate()

            palette.setColor1()
        }
    }

    private fun Palette.setColor1() {
        if (swatches.size < 2) return

        val min = swatches.minBy { it.rgb.getDarkness() }
        val max = swatches.maxBy { it.rgb.getDarkness() }
        val medium = dominantSwatch?.rgb ?: return

        if (medium - min.rgb < max.rgb - medium) {
            backgroundColor = min.rgb
            contentColor = max.rgb
            bodyTextColor = min.bodyTextColor
            titleColor = min.titleTextColor
        } else {
            backgroundColor = max.rgb
            contentColor = min.rgb
            bodyTextColor = max.bodyTextColor
            titleColor = max.titleTextColor
        }
    }

    companion object {
        fun Int.getDarkness(): Double {
            return ColorUtils.calculateLuminance(this)
        }

        fun Int.getContainer(): Int {
            return if (this.getDarkness() < 0.25) {
                this.darken(1.1F)
            } else {
                this.darken(0.95F)
            }
        }

        private fun Int.darken(factor: Float): Int {
            return Color.HSVToColor(FloatArray(3).apply {
                Color.colorToHSV(this@darken, this)
                this[2] *= factor
            })
        }
    }
}