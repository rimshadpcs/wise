package com.intractable.simm.view.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.intractable.simm.services.NotificationManager2
import com.intractable.simm.utils.Constants
import com.intractable.simm.view.activities.SessionActivity
import com.intractable.simm.viewmodel.SessionViewModel

class NotificationFragment: FragmentOperation() {
    override val eventPrefix: String
        get() {
            return "simm_"+this.javaClass.simpleName
        }
    private val viewModel: SessionViewModel by activityViewModels()
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private var RETRY_FLAG = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getEvent().observe(context as SessionActivity) {
            if (it == SessionViewModel.EVENT_SHOW_NOTIFICATION_BAR) {
                if (RETRY_FLAG == 2)
                    showSkipButton()
                else
                    RETRY_FLAG += 1
            }
        }
    }

    override fun startData() {
        viewModel.startOperationData()
    }

    override fun doAction() {
        SessionViewModel.progressSession = viewModel.progressLiveData.value!!
        Constants.notification_session_type = viewModel.operationDataLiveData.value!!.sessionType
        val notificationWorker: WorkRequest = OneTimeWorkRequestBuilder<NotificationManager2>().build()
        WorkManager
            .getInstance(context as SessionActivity)
            .enqueue(notificationWorker)

        Firebase.analytics.logEvent(eventPrefix + "_action_" + viewModel.operationDataLiveData.value!!.sessionType, null)
    }

    override fun nextPage() {
        viewModel.nextOperationPage()
    }

//    override fun editText() {
//        // not needed
//    }

    override fun onResume() {
        super.onResume()
        if (RETRY_FLAG == 3 && viewModel.operationDataLiveData.value!!.shouldDoAction){
            showSkipButton()
        }
    }
}