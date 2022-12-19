package com.intractable.simm.utils

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.Intent.EXTRA_CHOSEN_COMPONENT
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi


private const val TAG = "MyBroadcastReceiver"

class ApplicationSelectorReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {


        val clickedComponent: ComponentName = intent.getParcelableExtra(EXTRA_CHOSEN_COMPONENT)!!

        Log.e("clickedComponent", clickedComponent.toString())

    }


}
