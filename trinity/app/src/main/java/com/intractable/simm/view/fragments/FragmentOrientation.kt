package com.intractable.simm.view.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.intractable.simm.R
import com.intractable.simm.databinding.FragmentOrientationBinding
import com.intractable.simm.utils.Constants
import com.intractable.simm.view.activities.SessionActivity
import com.intractable.simm.viewmodel.SessionViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FragmentOrientation : Fragment() {

    val TAG = "Fragment Orientation"
    protected lateinit var orientationBinding: FragmentOrientationBinding
    private val viewModel: SessionViewModel by activityViewModels()
    var flag = 0

    val eventPrefix: String
        get() {
            return "simm_" + this.javaClass.simpleName
        }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        orientationBinding = FragmentOrientationBinding.inflate(inflater, container, false)
        var view = orientationBinding.root
        // Inflate the layout for this fragment
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR

        viewModel.orientationLiveData.observe(context as SessionActivity){
            if (it != null) {
                orientationBinding.tvTxt.text = it.text
                orientationBinding.ivImg.setImageResource(it.img)
                orientationBinding.tvTxt2.text = it.text
                orientationBinding.ivImg2.setImageResource(it.img)
                orientationBinding.tvTxt3.text = it.text
                try {
                    val animationFadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in)
                    //starting the animation
                    orientationBinding.tvTxt3.startAnimation(animationFadeIn)
                }catch (e:NullPointerException){
                    Log.e(TAG,e.toString())
                }
                if (!it.shouldDoAction) {
                    lifecycleScope.launch {
                        delay(2000)
                        orientationBinding.btContinue.visibility = View.VISIBLE
                    }
                }
            }
        }
        orientationBinding.btContinue.setOnTouchListener { _, motionEvent: MotionEvent ->
            when (motionEvent.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    orientationBinding.btContinue.setBackgroundResource(R.drawable.no_shadow_bg_green)
                    val params =
                        orientationBinding.btContinue.layoutParams as ViewGroup.MarginLayoutParams
                    params.bottomMargin = params.bottomMargin - 15
                    orientationBinding.btContinue.layoutParams = params
                }

                MotionEvent.ACTION_UP -> {
                    orientationBinding.btContinue.setBackgroundResource(R.drawable.shadow_button_bg)
                    val params =
                        orientationBinding.btContinue.layoutParams as ViewGroup.MarginLayoutParams
                    params.bottomMargin = params.bottomMargin + 15
                    orientationBinding.btContinue.layoutParams = params
                }
            }
            false
        }
        orientationBinding.btContinue.setOnClickListener {
            Firebase.analytics.logEvent(eventPrefix + "_completed", null)
            viewModel.checkForNextQuestion(true)
            viewModel.changeProgress()
        }

        viewModel.startOrientationData()

        Firebase.analytics.logEvent(eventPrefix + "_started", null)

        return view
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (flag < 2){
            flag++
            viewModel.nextOrientationPage()
        }
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            orientationBinding.layoutPortrait.visibility = View.GONE
            orientationBinding.layoutLand.visibility = View.VISIBLE
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            orientationBinding.tvTxt.visibility = View.GONE
            orientationBinding.ivImg.visibility = View.GONE
            orientationBinding.layoutPortrait.visibility = View.VISIBLE
            orientationBinding.lastpage.visibility = View.VISIBLE
            orientationBinding.layoutLand.visibility = View.GONE
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }


}