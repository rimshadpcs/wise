package com.intractable.simm.view.fragments

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.transition.TransitionInflater
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.TextViewCompat
import androidx.core.widget.TextViewCompat.AutoSizeTextType
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.intractable.simm.R
import com.intractable.simm.databinding.FragmentOperationBinding
import com.intractable.simm.view.activities.SessionActivity
import com.intractable.simm.viewmodel.SessionViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


abstract class FragmentOperation : Fragment() {
    protected lateinit var operationBinding: FragmentOperationBinding
    private val viewModel: SessionViewModel by activityViewModels()
    private var countDownTimer: CountDownTimer? = null
    protected var animation:Animation? = null
    protected var taskIsActive = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.transition_right)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        operationBinding = FragmentOperationBinding.inflate(inflater, container, false)
        val view = operationBinding.root
        animation = AnimationUtils.loadAnimation(context as SessionActivity,R.anim.slide_right)

        operationBinding.btContinue.setOnClickListener {
            nextPage()
        }

        operationBinding.btSkip.setOnClickListener {
            Firebase.analytics.logEvent(eventPrefix + "_skipped", null)
            viewModel.checkForNextQuestion(false)
        }

        viewModel.getEvent().observe(context as SessionActivity){
            if (it == SessionViewModel.EVENT_SHOW_SKIP_BUTTON){
                showSkipButton()
            }
        }

        operationBinding.btContinue.setOnTouchListener { _, motionEvent: MotionEvent ->
            when (motionEvent.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    operationBinding.btContinue.setBackgroundResource(R.drawable.no_shadow_bg_green)
                    val params =
                        operationBinding.btContinue.layoutParams as ViewGroup.MarginLayoutParams
                    params.bottomMargin = params.bottomMargin - 15
                    operationBinding.btContinue.layoutParams = params
                }

                MotionEvent.ACTION_UP -> {
                    operationBinding.btContinue.setBackgroundResource(R.drawable.shadow_button_bg)
                    val params =
                        operationBinding.btContinue.layoutParams as ViewGroup.MarginLayoutParams
                    params.bottomMargin = params.bottomMargin + 15
                    operationBinding.btContinue.layoutParams = params
                }
            }
            false
        }

        operationBinding.btSkip.setOnTouchListener { _, motionEvent: MotionEvent ->
            when (motionEvent.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    operationBinding.btSkip.setBackgroundResource(R.drawable.no_shadow_blue_button)
                    val params =
                        operationBinding.btSkip.layoutParams as ViewGroup.MarginLayoutParams
                    params.bottomMargin = params.bottomMargin - 15
                    operationBinding.btSkip.layoutParams = params
                }

                MotionEvent.ACTION_UP -> {
                    operationBinding.btSkip.setBackgroundResource(R.drawable.shadow_button_blue)
                    val params =
                        operationBinding.btSkip.layoutParams as ViewGroup.MarginLayoutParams
                    params.bottomMargin = params.bottomMargin + 15
                    operationBinding.btSkip.layoutParams = params
                }
            }
            false
        }



        Firebase.analytics.logEvent(eventPrefix + "_started", null)

        var mp : MediaPlayer = MediaPlayer.create(context, R.raw.system_action_creating)
        var shouldPlaySuccessSound = false

        viewModel.operationDataLiveData.observe(context as SessionActivity){
            operationBinding.btContinue.visibility = View.INVISIBLE
            countDownTimer = object : CountDownTimer(5000, 50) {

                override fun onTick(millisUntilFinished: Long) {
                    operationBinding.progressbar.incrementProgressBy(-1)
                }

                override fun onFinish() {
                    if (it.shouldDoAction){
                        operationBinding.progressbar.visibility = View.GONE
                    }
                    else{
                        nextPage()
                    }

                    Log.e("notPage",viewModel.operationPage.toString())
                }
            }
            if (it != null){
                if (it.shouldGoToNextActivity) {
                    EndSession()
                    return@observe
                }
                else {
                    operationBinding.progressbar.progress = 100
                    if (countDownTimer != null)
                        countDownTimer!!.cancel()
                    changeTheText(it.text)
                    operationBinding.ivCharacter.setImageResource(it.img)
                    if (it.hasCountDown) {
                        operationBinding.tvTxt.startAnimation(animation)
                        operationBinding.progressbar.visibility = View.VISIBLE
                        countDownTimer!!.start()
                        operationBinding.btContinue.visibility = View.INVISIBLE
                    }
                    else {
                        operationBinding.progressbar.visibility = View.GONE
                            val animationFadeIn =
                                AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
                            //starting the animation
                            operationBinding.tvTxt.startAnimation(animationFadeIn)
                        if (!it.shouldDoAction) {
                            operationBinding.btSkip.visibility = View.INVISIBLE
                            lifecycleScope.launch {
                                delay(2000)
                                operationBinding.btContinue.visibility = View.VISIBLE
                            }
                        }
                    }
                }
                if(shouldPlaySuccessSound){
                    // play sound success
                    if(mp.isPlaying) {
                        mp.pause() // Pause the current track
                    }
                    mp.release()
                        mp = MediaPlayer.create(context, R.raw.system_action_done)
                        mp.start()  // play sound
                    shouldPlaySuccessSound = false
                }
                if (it.shouldDoAction) {
                    // play sound building
                    if(mp.isPlaying) {
                        mp.pause() // Pause the current track
                    }
                    mp.start()  // play sound

                    shouldPlaySuccessSound = true
                    doAction()
                }
            }
        }
        startData()
        return view
    }

    fun EndSession() {
        operationBinding.frameBox.visibility = View.GONE
        operationBinding.ivCharacter.setImageDrawable(null)
        viewModel.checkForNextQuestion(true)
        Firebase.analytics.logEvent(eventPrefix + "_completed", null)
    }

    fun showSkipButton(){
        operationBinding.btSkip.visibility =View.VISIBLE
        changeTheText(viewModel.skipTextFlow)
        operationBinding.btContinue.visibility = View.INVISIBLE
        Log.e("skipText",viewModel.skipTextFlow)
    }

    fun changeTheText(string: String?){
        operationBinding.tvTxt.text = string

        editText()
    }

    abstract fun startData()

    abstract fun doAction()

    protected abstract fun nextPage()

    open fun editText() {}

    abstract val eventPrefix: String
}
