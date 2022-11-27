package com.intractable.simm.view.activities

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.intractable.simm.databinding.ActivityEndSessionBinding
import com.intractable.simm.R
import com.intractable.simm.utils.AppUtil
import com.intractable.simm.viewmodel.EndSessionViewModel
class EndSessionActivity : AppCompatActivity() {

    private lateinit var activitySessionBinding: ActivityEndSessionBinding
    lateinit var appUtil: AppUtil
    private val endSessionViewModel: EndSessionViewModel by viewModels()
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        activitySessionBinding = ActivityEndSessionBinding.inflate(layoutInflater)
        setContentView(activitySessionBinding.root)
        appUtil = AppUtil(this)
        appUtil.setDarkMode()

        endSessionViewModel.endSessionCelebration.observe(this){
            activitySessionBinding.lottieEndCeleb.setAnimation(it)
            firebaseAnalytics = Firebase.analytics


//            activitySessionBinding.tvFinish.visibility = View.INVISIBLE

        }

        endSessionViewModel.endSessionText.observe(this){
            activitySessionBinding.tvFinish.text = it
        }

        activitySessionBinding.lottieEndCeleb.addAnimatorListener(object : Animator.AnimatorListener{
            override fun onAnimationStart(p0: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                activitySessionBinding.btFinishSession.visibility =View.VISIBLE

            }

            override fun onAnimationCancel(p0: Animator) {
            }

            override fun onAnimationRepeat(p0: Animator) {
            }
        })


        var mp : MediaPlayer = MediaPlayer.create(this, R.raw.session_complete)
        if(mp.isPlaying) {
            mp.pause() // Pause the current track
        }
        mp.start()  // play sound

        activitySessionBinding.btFinishSession.setOnClickListener {
            if (mp.isPlaying) {
                mp.pause() // Pause the current track
            }
            mp.release()
            mp = MediaPlayer.create(this, R.raw.button_press)
            mp.start()  // play sound

            startActivity(Intent(this, HomeActivity::class.java))
            finishAffinity()
        }

        activitySessionBinding.btFinishSession.setOnTouchListener { _, motionEvent: MotionEvent ->
            when(motionEvent.actionMasked){
                MotionEvent.ACTION_DOWN-> {
                    activitySessionBinding.btFinishSession.setBackgroundResource(R.drawable.no_shadow_bg_green)
                    val params =
                        activitySessionBinding.btFinishSession.layoutParams as ViewGroup.MarginLayoutParams
                    params.bottomMargin = params.bottomMargin-15
                    activitySessionBinding.btFinishSession.layoutParams = params
                }
                MotionEvent.ACTION_UP -> {
                    activitySessionBinding.btFinishSession.setBackgroundResource(R.drawable.shadow_button_bg)
                    val params = activitySessionBinding.btFinishSession.layoutParams as ViewGroup.MarginLayoutParams
                    params.bottomMargin = params.bottomMargin+15
                    activitySessionBinding.btFinishSession.layoutParams = params
                }
            }
            false
        }

        // delay for button showing
        // not sure if this is the best way to do it
//        activitySessionBinding.btFinishSession.visibility = View.INVISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            activitySessionBinding.btFinishSession.visibility = View.VISIBLE
        }, 2000)

        endSessionViewModel.finishPlantStageAnimation()
    }


}