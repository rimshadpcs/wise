package com.intractable.simm.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.se.omapi.Session
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.transition.TransitionInflater
import com.intractable.simm.R
import com.intractable.simm.view.activities.SessionActivity
import com.intractable.simm.viewmodel.SessionViewModel
import android.view.animation.AnimationUtils
import androidx.lifecycle.lifecycleScope
import com.intractable.simm.databinding.ActivitySettingsBinding.inflate
import com.intractable.simm.databinding.FragmentExplanationBinding
import com.intractable.simm.databinding.FragmentStoryboardBinding.inflate
import com.intractable.simm.gamelogic.Config
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class FragmentExplanation : Fragment() {

    private lateinit var explanationBinding: FragmentExplanationBinding
    private val explanationModel: SessionViewModel by activityViewModels()


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

        explanationBinding = FragmentExplanationBinding.inflate(inflater, container, false)


        val view = explanationBinding.root

        explanationModel.instructionData.observe(context as SessionActivity) {
            if (it.img != 0) {
                explanationBinding.ivSimmExplaining.setAnimation(it.img)
                explanationBinding.ivSimmExplaining.playAnimation()
                explanationBinding.tvAnimExplanation.text = it.text
                explanationBinding.tvExplanation.visibility = View.GONE
            } else {
                explanationBinding.ivSimmAnimExplaining.visibility = View.GONE
                explanationBinding.tvExplanation.text = it.text
            }

            lifecycleScope.launch(Dispatchers.Main) {
                delay(1500)
                explanationBinding.btComplete.visibility = View.VISIBLE
            }
        }

        explanationBinding.btComplete.setOnClickListener {

            explanationModel.shouldSparkle = false
            explanationModel.changeProgress()
            explanationModel.checkForNextQuestion(true)
        }

        explanationBinding.btComplete.setOnTouchListener { _, motionEvent: MotionEvent ->
            when (motionEvent.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    explanationBinding.btComplete.setBackgroundResource(R.drawable.no_shadow_bg_green)
                    val params =
                        explanationBinding.btComplete.layoutParams as ViewGroup.MarginLayoutParams
                    params.bottomMargin = params.bottomMargin - 15
                    explanationBinding.btComplete.layoutParams = params
                }

                MotionEvent.ACTION_UP -> {
                    explanationBinding.btComplete.setBackgroundResource(R.drawable.shadow_button_bg)
                    val params =
                        explanationBinding.btComplete.layoutParams as ViewGroup.MarginLayoutParams
                    params.bottomMargin = params.bottomMargin + 15
                    explanationBinding.btComplete.layoutParams = params
                }
            }
            false
        }
        AnimationUtils.loadAnimation(context as SessionActivity, R.anim.slide_right)


        return view
    }


}