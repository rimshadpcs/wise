package com.intractable.simm.utils

import android.animation.Animator
import android.media.AudioManager
import android.media.MediaPlayer
import com.airbnb.lottie.LottieAnimationView
import com.intractable.simm.view.activities.HomeActivity
import java.util.*

class AnimationControllerImpl:AnimationController {


    private var queue: Queue<Pair<LottieAnimationView, MediaPlayer>> = LinkedList()
//    private lateinit var anim: Pair<LottieAnimationView?, MediaPlayer?>
    lateinit var endsAnimationHandler: () -> Unit
//    lateinit var animationAndSound: Pair<LottieAnimationView, MediaPlayer>

    override fun addAnimation(lottieAnimationView: LottieAnimationView, mediaPlayer: MediaPlayer) {
//        anim.first?.animation = lottieAnimationView.animation
//        anim.second?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        val anim = Pair<LottieAnimationView, MediaPlayer>(lottieAnimationView, mediaPlayer)

        queue.add(anim)

        anim.first.addAnimatorListener(object : Animator.AnimatorListener{
            override fun onAnimationStart(p0: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {

                if(queue.isNotEmpty()){

                    val anim1 = queue.poll() as Pair<LottieAnimationView, MediaPlayer>
                    anim1.first.playAnimation()
                    anim1.second.start()
                } else{
                    endsAnimationHandler()
                }


            }

            override fun onAnimationCancel(p0: Animator) {
            }

            override fun onAnimationRepeat(p0: Animator) {
            }
        })
    }

    override fun setListener(endsAnimationHandler: () -> Unit) {
        this.endsAnimationHandler = endsAnimationHandler
    }

    override fun play() {
        val anim1 = queue.poll() as? Pair<LottieAnimationView, MediaPlayer>

        anim1?.first?.playAnimation()
        anim1?.second?.start()

//        if (mpEG.isPlaying) {
////                        mpEG.pause() // Pause the current track
////                    }

    }


}