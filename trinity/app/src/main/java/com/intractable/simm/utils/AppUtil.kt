package com.intractable.simm.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context.WINDOW_SERVICE
import android.content.res.Configuration
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatDelegate
import androidx.camera.core.impl.utils.ContextUtil.getBaseContext
import androidx.core.content.ContextCompat.getSystemService
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.intractable.simm.R


class AppUtil {
    lateinit var activity: Activity
    var cancelSession: (()->Unit)? = null

    constructor(activity: Activity, cancelSession: (()->Unit)? = null) {
        this.activity = activity
        this.cancelSession = cancelSession
    }

    constructor()

    fun showExitDialog() {

        val viewGroup = activity.findViewById<ViewGroup>(android.R.id.content)

        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_close, viewGroup, false)
        val builder = AlertDialog.Builder(activity)

        builder.setView(dialogView)

        val alertDialog = builder.create()

        val btNo = dialogView.findViewById<FrameLayout>(R.id.bt_no)
        val btYes = dialogView.findViewById<FrameLayout>(R.id.bt_yes)
        alertDialog.show()
        btNo.setOnClickListener {
            Firebase.analytics.logEvent("simm_session_cancel_no", null)
            alertDialog.dismiss()
        }
        btYes.setOnClickListener {
            cancelSession?.invoke()
            Firebase.analytics.logEvent("simm_session_cancel_yes", null)
            activity.finish()
            alertDialog.dismiss()
        }
    }


    fun setDarkMode(){
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }




}