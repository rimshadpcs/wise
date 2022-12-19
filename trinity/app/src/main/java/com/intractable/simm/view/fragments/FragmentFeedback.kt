package com.intractable.simm.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.transition.TransitionInflater
import com.intractable.simm.R
import com.intractable.simm.databinding.FragmentFeedbackBinding
import com.intractable.simm.view.activities.SessionActivity
import com.intractable.simm.viewmodel.SessionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FragmentFeedback : Fragment() {
    private lateinit var feedbackBinding: FragmentFeedbackBinding
    private val feedbackModel: SessionViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.transition_right)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        feedbackBinding = FragmentFeedbackBinding.inflate(inflater, container, false)
        val view = feedbackBinding.root

        feedbackModel.instructionData.observe(context as SessionActivity) {
            feedbackBinding.tvFeedbak.text = it.text

            if (it.isAnimation) {
                feedbackBinding.ivSimmEncouraging.setAnimation(it.img)
                feedbackBinding.ivSimmEncouraging.playAnimation()
            } else {
                feedbackBinding.ivSimmEncouraging.setImageResource(it.img)
            }

            lifecycleScope.launch(Dispatchers.Main){

                delay(1500)
                feedbackBinding.btContinue.visibility =View.VISIBLE
            }
        }

        feedbackBinding.btContinue.setOnClickListener {
            feedbackModel.checkForNextQuestion(true)
        }
        feedbackBinding.btContinue.setOnTouchListener { _, motionEvent: MotionEvent ->
            when (motionEvent.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    feedbackBinding.btContinue.setBackgroundResource(R.drawable.no_shadow_bg_green)
                    val params =
                        feedbackBinding.btContinue.layoutParams as ViewGroup.MarginLayoutParams
                    params.bottomMargin = params.bottomMargin - 15
                    feedbackBinding.btContinue.layoutParams = params
                }

                MotionEvent.ACTION_UP -> {
                    feedbackBinding.btContinue.setBackgroundResource(R.drawable.shadow_button_bg)
                    val params =
                        feedbackBinding.btContinue.layoutParams as ViewGroup.MarginLayoutParams
                    params.bottomMargin = params.bottomMargin + 15
                    feedbackBinding.btContinue.layoutParams = params
                }
            }
            false
        }
        AnimationUtils.loadAnimation(context as SessionActivity, R.anim.slide_right)

        return view
    }

}
