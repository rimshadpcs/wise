package com.intractable.simm.view.activities

import android.animation.Animator
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Fade
import android.transition.Transition
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieCompositionFactory
import com.intractable.simm.R
import com.intractable.simm.databinding.ActivityPlantAnimationBinding
import com.intractable.simm.utils.Plants
import com.intractable.simm.viewmodel.EndSessionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlantAnimationActivity : AppCompatActivity() {

    private lateinit var plantAnimationBinding: ActivityPlantAnimationBinding
    private val endSessionViewModel: EndSessionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        plantAnimationBinding = ActivityPlantAnimationBinding.inflate(layoutInflater)
        setContentView(plantAnimationBinding.root)
        val mp : MediaPlayer = MediaPlayer.create(this, R.raw.plant_pop_delay_100s)

        animationPlayed = 0

        endSessionViewModel.plantStageComment.observe(this){
            plantAnimationBinding.tvPlantStageDescription.text =it

            val activity = this
            lifecycleScope.launch (Dispatchers.Main){
                delay(1000)

                plantAnimationBinding.tvPlantStageDescription.fadeVisibility(View.VISIBLE)

                delay(4000)
                startActivity(Intent(activity, EndSessionActivity::class.java))

                finish()
            }

        }


        plantAnimationBinding.ivSimmFinish.addAnimatorListener(object : Animator.AnimatorListener{
            override fun onAnimationStart(p0: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                playAnimations()
            }

            override fun onAnimationCancel(p0: Animator) {
            }

            override fun onAnimationRepeat(p0: Animator) {
            }
        })
        endSessionViewModel.plantStageAnimationFlow.observe(this) {
            if (it != null) {
                plantAnimationBinding.ivSimmFinish.setAnimation(it.first!!)
                plantAnimationBinding.ivSimmFinish.playAnimation()
                secondAnimation = it.second!!
                animationPlayed = 1
                endSessionViewModel.finishPlantStageAnimation()

                if(mp.isPlaying) {
                    mp.pause() // Pause the current track
                }
                mp.start()  // play sound
            }
        }

    }
    fun View.fadeVisibility(visibility: Int, duration: Long = 1000) {
        val transition: Transition = Fade()
        transition.duration = duration
        transition.addTarget(this)
        TransitionManager.beginDelayedTransition(this.parent as ViewGroup, transition)
        this.visibility = visibility
    }

    var animationPlayed: Int = 0
    var secondAnimation: Int = 0
    fun playAnimations(){
        if(animationPlayed == 1){
            LottieCompositionFactory.fromRawResSync(this, secondAnimation)?.let { result ->
            result.value?.let { composition -> plantAnimationBinding.ivSimmFinish.setComposition(composition) }
        }
//            plantAnimationBinding.ivSimmFinish.setAnimation(secondAnimation)
            plantAnimationBinding.ivSimmFinish.playAnimation()
            animationPlayed = 2
        }
    }
}