package com.intractable.simm.view.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.intractable.simm.utils.Constants
import com.intractable.simm.view.activities.SessionActivity
import com.intractable.simm.viewmodel.SessionViewModel

class QSettingsFragment:FragmentOperation() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    override val eventPrefix: String
        get() {
            return "simm_"+this.javaClass.simpleName
        }

    private var RETRY_FLAG = 0


    private val viewModel: SessionViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getEvent().observe(context as SessionActivity){
            if (it == SessionViewModel.EVENT_SHOW_NOTIFICATION_BAR) {
                if (RETRY_FLAG == 2)
                    showSkipButton()
                else
                    RETRY_FLAG += 1
            }
        }

        viewModel.hintFlow.observe(context as SessionActivity) {
            if (it != null) {
                changeTheText(it)
                RETRY_FLAG = 0
            }
        }
    }

    override fun startData() {
        viewModel.startOperationData()
    }

    override fun doAction() {

    }

    override fun nextPage() {
        viewModel.nextOperationPage()
    }


    override fun onResume() {
        super.onResume()
        if (RETRY_FLAG == 3 && !Constants.QSClicked)
            showSkipButton()
    }

    companion object{
        fun newInstance(): QSettingsFragment{
            return QSettingsFragment()
        }
    }
}