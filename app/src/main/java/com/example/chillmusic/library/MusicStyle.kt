package com.example.chillmusic.library

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.ColorUtils
import androidx.palette.graphics.Palette
import com.example.chillmusic.constant.log
import kotlin.math.roundToInt

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
            val palette = Palette.Builder(it).clearFilters().generate()
            palette.setColor()
        }
    }

    private fun Palette.setColor() {
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

    val isDarkBackGround: Boolean
        get() = backgroundColor.getDarkness() < 0.5F

    companion object {
        fun Int.getDarkness(): Double {
            return ColorUtils.calculateLuminance(this)
        }

        fun Int.getContainer(): Int {
            val dark = this.getDarkness()
            val a = Color.alpha(this)
            val r = Color.red(this)
            val g = Color.green(this)
            val b = Color.blue(this)

            val factorValue = 0.05F
            val factor: (Int) -> Int = {
                val addValue = if(dark < 0.5F)
                    (it - 0) * factorValue + 10
                else
                    (it - 255) * factorValue - 8

                it + addValue.roundToInt()
            }

            return Color.argb(
                a,
                factor(r),
                factor(g),
                factor(b)
            )
        }
    }
}