package com.rimapps.NoteApp

import android.content.Context
import android.content.LocusId

class ResourseComparer {

    fun isEqual (context: Context, resId: Int, string: String):Boolean{
      return context.getString(resId) == string
    }
}