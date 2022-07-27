package com.example.simmone.utils

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatDelegate
import com.example.simmone.R
import org.json.JSONArray

class AppUtil {
    lateinit var activity: Activity

    constructor(activity: Activity) {
        this.activity = activity
    }

    constructor()

    fun showLoginDialog() {
        val viewGroup = activity.findViewById<ViewGroup>(android.R.id.content)

        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_close, viewGroup, false)


        val builder = AlertDialog.Builder(activity)

        builder.setView(dialogView)

        val alertDialog = builder.create()
        val bt_no = dialogView.findViewById<FrameLayout>(R.id.bt_no)
        val bt_Yes = dialogView.findViewById<FrameLayout>(R.id.bt_yes)
        alertDialog.show()
        bt_no.setOnClickListener {
            alertDialog.dismiss()
        }
        bt_Yes.setOnClickListener {
            activity.finish()
            alertDialog.dismiss()
        }
    }

    fun setDarkMode(){
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

}