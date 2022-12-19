package com.intractable.simm.view.fragments

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import com.google.firebase.analytics.FirebaseAnalytics
import com.intractable.simm.viewmodel.SessionViewModel


class NotificationSettingsFragment: FragmentOperation() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private val viewModel: SessionViewModel by activityViewModels()
    var flag = 0
    private var RETRY_FLAG = 0

    override val eventPrefix: String
        get(){
            return "simm_" + this.javaClass.simpleName
        }

    override fun startData() {
        viewModel.startOperationData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun doAction() {

        val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, "com.intractable.simm")
            putExtra(Settings.EXTRA_CHANNEL_ID, "channel_ID_1")
        }
        flag = 1
        startActivity(intent)
    }


    override fun nextPage() {
        viewModel.nextOperationPage()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        if (flag == 1) {
            var notificationManager: NotificationManager =
                context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            var channel = notificationManager.getNotificationChannel("channel_ID_1")
            if (!channel.shouldVibrate()) {
                changeTheText(viewModel.operationDataLiveData.value!!.textError)
                if (RETRY_FLAG == 3)
                    showSkipButton()
            }
            else {
                flag = 0
                nextPage()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (flag == 1)
            RETRY_FLAG += 1
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}