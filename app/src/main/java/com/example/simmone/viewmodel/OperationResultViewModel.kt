package com.example.simmone.viewmodel

import android.database.Observable
import androidx.lifecycle.ViewModel
import javax.inject.Inject
import javax.inject.Named

class OperationResultViewModel @Inject constructor(

    @Named("outputMessageForNotification")outputMessageForNotification : String,
    @Named("NextButton")NextButton : String

) : ViewModel() {


}