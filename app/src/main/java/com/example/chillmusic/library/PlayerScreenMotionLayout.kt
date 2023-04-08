package com.example.chillmusic.library

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import com.example.chillmusic.R


/**
 * This is where all the magic happens.
 * This View Takes up the entire screen.
 * The background of this view is actually transparent and we resize `player_background_view`
 * based on user touch. The player with the white background is what is resized, but this
 * PlayerScreenMotionLayout always takes up the entire screen.
 * So when you touch the Fragment when the player is minimized, you are actually touching this layout.
 * We calculate whether the touch is on the Mini player or not and based on that we pass the toucch to
 * parent or consume it
 */
class PlayerScreenMotionLayout(
    context: Context,
    attributeSet: AttributeSet? = null
) : MotionLayout(context, attributeSet) {
    private val viewToDetectTouch by lazy {
        this.findViewById<View>(R.id.container)
    }
    private val viewRect = Rect()
    private var hasTouchStarted = false

    init {
        addTransitionListener(object : TransitionListener {
            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}

            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                hasTouchStarted = false
            }
        })
    }

    private val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            if (currentState == R.id.minimize)
                transitionToState(R.id.expanded)
            return false
        }
    })


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event) //This ensures the Mini Player is maximised on single tap
        when (event.actionMasked) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                hasTouchStarted = false
                return super.onTouchEvent(event)
            }
        }
        if (!hasTouchStarted) {
            viewToDetectTouch.getHitRect(viewRect)
            hasTouchStarted = viewRect.contains(event.x.toInt(), event.y.toInt())
        }
        return hasTouchStarted && super.onTouchEvent(event)
    }
}