package com.example.simmone.utils

import android.annotation.SuppressLint
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import kotlin.math.abs

class SwipeServices : View.OnTouchListener {
    enum class SwipeDirection {
        bottomToTop
    }

    private var rootLayout: ViewGroup? = null
    private var layoutToShowHide: ViewGroup? = null
    private var gestureDetector: GestureDetector? = null
    private var swipeDirections: MutableList<SwipeDirection>? = null

    fun initialize(rootLayout: ViewGroup, layoutToShowHide:ViewGroup?, swipeDirections: MutableList<SwipeDirection>,maxSwipeDistance: Int = 1) {
        val gestureListener = GestureListener()
        gestureDetector = GestureDetector(rootLayout.context, gestureListener)
        this.rootLayout = rootLayout
        this.layoutToShowHide = layoutToShowHide
        this.swipeDirections = swipeDirections
        gestureListener.MAX_SWIPE_DISTANCE = maxSwipeDistance
        this.rootLayout!!.setOnTouchListener(this)
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        return gestureDetector?.onTouchEvent(event)!!
    }

    inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        var MAX_SWIPE_DISTANCE = 1
        private val SWIPE_VELOCITY_THRESHOLD = 1

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {

            var result = false
            try {
                val diffY = e2.y - e1.y
                if (abs(diffY) > MAX_SWIPE_DISTANCE && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD)  {
                        if (diffY<0) {
                            onSwipeBottomToTop()
                        }
                        }
                result = true
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
            return result
        }
    }

    fun cancel() {
        layoutToShowHide?.animate()?.scaleX(1f)?.scaleY(1f)?.setDuration(500)?.start();
    }

        fun onSwipeBottomToTop() {
        layoutToShowHide?.animate()?.scaleX(.7f)?.scaleY(.7f)?.setDuration(500)?.start();
        }


}

