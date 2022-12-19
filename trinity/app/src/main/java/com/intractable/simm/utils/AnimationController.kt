package com.intractable.simm.utils

import android.media.MediaPlayer
import com.airbnb.lottie.LottieAnimationView

interface AnimationController {
    fun addAnimation(lottieAnimationView: LottieAnimationView, mediaPlayer: MediaPlayer)

    fun setListener(endsAnimationHandler:()->Unit)

    fun play()
}