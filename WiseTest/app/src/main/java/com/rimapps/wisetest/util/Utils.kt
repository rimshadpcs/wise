package com.rimapps.wisetest.util

import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import java.time.Duration

fun Fragment.showsnackBar(
    message: String,
    duration: Int = Snackbar.LENGTH_SHORT,
    view:View =  requireView()
){
    Snackbar.make(view, message,duration).show()
}

val <T> T.exhaustive : T
get() = this