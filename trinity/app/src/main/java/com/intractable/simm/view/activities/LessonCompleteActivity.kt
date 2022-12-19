package com.intractable.simm.view.activities

import android.animation.Animator
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.intractable.simm.R
import com.intractable.simm.databinding.ActivityLessonCompleteAnimationBinding
import com.intractable.simm.viewmodel.EndSessionViewModel

class LessonCompleteActivity : AppCompatActivity() {

    private lateinit var lessonCompleteAnimationBinding: ActivityLessonCompleteAnimationBinding
    private val endSessionViewModel: EndSessionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lessonCompleteAnimationBinding = ActivityLessonCompleteAnimationBinding.inflate(layoutInflater)
        setContentView(lessonCompleteAnimationBinding.root)
        var mp : MediaPlayer = MediaPlayer.create(this, R.raw.activity_end_sound)

        val activity = this
        lessonCompleteAnimationBinding.lessonCompleteAnimation.addAnimatorListener(object : Animator.AnimatorListener{
            override fun onAnimationStart(p0: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                endSessionViewModel.plantStageAnimationFlow.observe(activity) {
                    if (it != null) {
                        if (it == Pair(0,0)) {
                            startActivity(
                                Intent(activity, EndSessionActivity::class.java)
                            )
                        }
                        else {
                            startActivity(
                                Intent(activity, PlantAnimationActivity::class.java)
                            )
                        }
                        finish()
                    }
                }
            }

            override fun onAnimationCancel(p0: Animator) {
            }

            override fun onAnimationRepeat(p0: Animator) {
            }
        })

        endSessionViewModel.plantStageAnimationFlow.observe(this) { plantPair ->
            if (plantPair != null) {
                endSessionViewModel.lessonCompleteAnimation.observe(this){
                    lessonCompleteAnimationBinding.lessonCompleteAnimation.setAnimation(it)
                    lessonCompleteAnimationBinding.lessonCompleteAnimation.playAnimation()
                }

                if(mp.isPlaying) {
                    mp.pause() // Pause the current track
                }
                mp.start()  // play sound
            }
        }
    }
}