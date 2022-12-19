package com.intractable.simm.view.fragments

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.intractable.simm.databinding.FragmentOperationBinding
import com.intractable.simm.services.NotificationManager2
import com.intractable.simm.utils.Constants
import com.intractable.simm.view.activities.SessionActivity
import com.intractable.simm.viewmodel.SessionViewModel

class ScreenLockTimeFragment: FragmentOperation() {
    override val eventPrefix: String
        get() {
            return "simm_"+this.javaClass.simpleName
        }
    private val viewModel: SessionViewModel by activityViewModels()
    private lateinit var firebaseAnalytics: FirebaseAnalytics



    private var screenTimeoutTime = 0
    private var screenLockTime = 0

    private var pauseFlag = 0
    private var actionFlag = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        screenTimeoutTime = Settings.System.getInt(requireContext().contentResolver, Settings.System.SCREEN_OFF_TIMEOUT)
        screenLockTime = Settings.Secure.getInt(requireContext().contentResolver, "lock_screen_lock_after_timeout", 5000)
        doActionState = 0

    }

    override fun startData() {
        viewModel.startOperationData()
    }

    var doActionState: Int = 0
    override fun doAction() {
        SessionViewModel.progressSession = viewModel.progressLiveData.value!!
        pauseFlag = 1
        actionFlag = 1
        // show where the settings are, first time doAction is called, and show where the other settings are when doAction is called for the second time
        if(doActionState == 0){
            // go to screen timeout settings

            // ACTION_SECURITY_SETTINGS - brings you to the page where you can tap on screen lock, which leads to be able to tap on lock after screen timeout
            // ACTION_DISPLAY_SETTINGS - brings you to the page where you can tap on screen timeout


            Log.d("ScreenLockFragment", "Do action 0")
            val intent = Intent(Settings.ACTION_DISPLAY_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, "com.intractable.simm")
                putExtra(Settings.EXTRA_CHANNEL_ID, "channel_ID_1")
            }
            startActivity(intent)
            doActionState++
        }
        else {
            // go to passcode lock time settings
            Log.d("ScreenLockFragment", "Do action 1")
            val intent = Intent(Settings.ACTION_SECURITY_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, "com.intractable.simm")
                putExtra(Settings.EXTRA_CHANNEL_ID, "channel_ID_1")
            }
            startActivity(intent)
            doActionState++
        }

        Firebase.analytics.logEvent(eventPrefix + "_action_" + viewModel.operationDataLiveData.value!!.sessionType, null)
    }

    override fun nextPage() {
        viewModel.nextOperationPage()
    }

    override fun editText(){
        var displayedText: String = operationBinding.tvTxt.text as String
        displayedText = displayedText.replace("%timeout", "" + msToString(screenTimeoutTime))
        displayedText = displayedText.replace("%lockTime", "" + msToString(screenLockTime))
        operationBinding.tvTxt.text = displayedText
    }

    fun msToString(inputTimeMS: Int): String{
        if(inputTimeMS == 0){
            return "0 seconds"
        }
        val oneMinute = 60 * 1000
        if(inputTimeMS < oneMinute){  // display in seconds
            if(inputTimeMS%1000 == 0){
                return (inputTimeMS/1000).toString() + " seconds"
            }
            else{
                return "around " + (inputTimeMS/1000).toString() + " seconds"
            }
        }
        else{  // display in minutes
            if(inputTimeMS%oneMinute == 0){
                return (inputTimeMS/oneMinute).toString() + " minutes"
            }
            else{
                return (inputTimeMS/oneMinute).toString() + " minutes and " + ((inputTimeMS%oneMinute)/1000).toString() + " seconds"
            }
        }

    }

    override fun onResume() {
        super.onResume()
        if(pauseFlag == 1){
            pauseFlag = 0
            if(actionFlag == 1){
                actionFlag = 0

                if(doActionState == 1){  // if the variable has not changed, skip showing the time again
                    if(Settings.System.getInt(requireContext().contentResolver, Settings.System.SCREEN_OFF_TIMEOUT) == screenTimeoutTime){
                        nextPage()
                    }
                    else{  // otherwise, save the new time
                        screenTimeoutTime = Settings.System.getInt(requireContext().contentResolver, Settings.System.SCREEN_OFF_TIMEOUT)
                    }
                }
                else if(doActionState == 2){  // if the variable has not changed, skip showing the time again
                    if(Settings.Secure.getInt(requireContext().contentResolver, "lock_screen_lock_after_timeout", 5000) == screenLockTime){
                        nextPage()
                    }
                    else{  // otherwise, save the new time
                        screenLockTime = Settings.Secure.getInt(requireContext().contentResolver, "lock_screen_lock_after_timeout", 5000)
                    }
                }
            }
            nextPage()
        }
    }
}