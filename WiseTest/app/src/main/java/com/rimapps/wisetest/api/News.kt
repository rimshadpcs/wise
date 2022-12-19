package com.rimapps.wisetest.api

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


data class News (
    val title: String?,
    val url:String,
    val image: String?)
