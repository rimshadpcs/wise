package com.intractable.simm.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.transition.TransitionInflater
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.intractable.simm.R
import com.intractable.simm.databinding.FragmentRightOrWrongBinding
import com.intractable.simm.model.RightOrWrongItem
import com.intractable.simm.utils.Constants
import com.intractable.simm.view.activities.SessionActivity
import com.intractable.simm.viewmodel.SessionViewModel


class RightOrWrongFragment : Fragment(), RightBottomSheetDialog.RightBottomSheetListener, WrongBottomSheetDialog.WrongBottomSheetListener, View.OnClickListener {
    private lateinit var rightOrWrongBinding: FragmentRightOrWrongBinding
    private val rightOrWrongModel: SessionViewModel by activityViewModels()
    private var currentStatement = 0


    private lateinit var rightOrWrongItem: RightOrWrongItem


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
        rightOrWrongBinding = FragmentRightOrWrongBinding.inflate(inflater, container, false)
        val view = rightOrWrongBinding.root
        AnimationUtils.loadAnimation(context as SessionActivity, R.anim.slide_right)

        rightOrWrongModel.rightOrWrongData.observe(context as SessionActivity) {
            if (it != null) {
                rightOrWrongItem = it
                rightOrWrongModel.rightOrWrongItem = it
                setStatementOnPage()
                Firebase.analytics.logEvent("simm_statement_started", null)
            }
        }

        rightOrWrongBinding.tvTrue.setOnTouchListener { _, motionEvent: MotionEvent ->
            when (motionEvent.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    rightOrWrongBinding.tvTrue.setBackgroundResource(R.drawable.no_shadow_bg_green)
                    val params =
                        rightOrWrongBinding.tvTrue.layoutParams as ViewGroup.MarginLayoutParams
                    params.bottomMargin = params.bottomMargin - 15
                    rightOrWrongBinding.tvTrue.layoutParams = params
                }

                MotionEvent.ACTION_UP -> {
                    rightOrWrongBinding.tvTrue.setBackgroundResource(R.drawable.shadow_button_bg)
                    val params =
                        rightOrWrongBinding.tvTrue.layoutParams as ViewGroup.MarginLayoutParams
                    params.bottomMargin = params.bottomMargin + 15
                    rightOrWrongBinding.tvTrue.layoutParams = params
                }
            }
            false
        }
        rightOrWrongBinding.tvFalse.setOnTouchListener { _, motionEvent: MotionEvent ->
            when (motionEvent.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    rightOrWrongBinding.tvFalse.setBackgroundResource(R.drawable.no_shadow_red_button)
                    val params =
                        rightOrWrongBinding.tvFalse.layoutParams as ViewGroup.MarginLayoutParams
                    params.bottomMargin = params.bottomMargin - 15
                    rightOrWrongBinding.tvFalse.layoutParams = params
                }

                MotionEvent.ACTION_UP -> {
                    rightOrWrongBinding.tvFalse.setBackgroundResource(R.drawable.red_button_bg)
                    val params =
                        rightOrWrongBinding.tvFalse.layoutParams as ViewGroup.MarginLayoutParams
                    params.bottomMargin = params.bottomMargin + 15
                    rightOrWrongBinding.tvFalse.layoutParams = params
                }
            }
            false
        }

        return view
    }



    private fun setStatementOnPage() {

        rightOrWrongBinding.tvStatement.text = rightOrWrongItem.statement

        logStatementEnded()
        rightOrWrongBinding.tvTrue.setOnClickListener {
            validateTrueClick()
        }
        rightOrWrongBinding.tvFalse.setOnClickListener {
            validateFalseClick()
        }






    }

    private fun validateFalseClick() {

        val falseButton = rightOrWrongBinding.tvFalse.text.toString()

        logStatementEnded()
        if(falseButton.equals(rightOrWrongItem.answer, ignoreCase = true)){
            callRightBottomSheet()

        }
        else {
            callWrongBottomSheet()
        }

    }

    private fun logStatementEnded() {
        Firebase.analytics.logEvent("simm_statement_completed", null)
    }

    private fun validateTrueClick() {

        val trueButton = rightOrWrongBinding.tvTrue.text.toString()

        if (trueButton.equals(rightOrWrongItem.answer, ignoreCase = true)){
            callRightBottomSheet()

        }

        else {
            callWrongBottomSheet()
        }
    }

    private fun callRightBottomSheet() {
        rightOrWrongModel.currentStatement = currentStatement
        rightOrWrongModel.eventlivedata.value = SessionViewModel.EVENT_SHOW_RIGHT_BOTTOMSHEET_STATEMENT
        rightOrWrongModel.eventlivedata.value = SessionViewModel.EVENT_NONE


    }


    private fun callWrongBottomSheet() {
        rightOrWrongModel.currentStatement = currentStatement
        rightOrWrongModel.shouldSparkle = false
        rightOrWrongModel.eventlivedata.value = SessionViewModel.EVENT_SHOW_WRONG_BOTTOMSHEET_STATEMENT
        rightOrWrongModel.eventlivedata.value = SessionViewModel.EVENT_NONE

    }

    override fun onWrongButtonClicked(text: String?) {
        rightOrWrongModel.checkForNextStatement()
    }
    override fun onRightButtonClicked(text: String?) {
        rightOrWrongModel.checkForNextStatement()
    }


    override fun onClick(v: View?) {
    }


}

