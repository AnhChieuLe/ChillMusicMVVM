package com.example.chillmusic.library

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import com.example.chillmusic.R
import kotlin.math.abs

class MusicMotionLayout(
    context: Context,
    attributeSet: AttributeSet? = null
) : MotionLayout(context, attributeSet) {
    private val container: View = findViewById(R.id.container)
    private var inTransition: Boolean = false

    init {
        addTransitionListener(object : TransitionListener {
            override fun onTransitionTrigger(motionLayout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) {}

            override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) {}

            override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) { inTransition = true }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) { inTransition = false }
        })
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        return super.onInterceptTouchEvent(event)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(ev)
    }

    private val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener(){
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            return super.onSingleTapConfirmed(e)
        }
    })

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }
}