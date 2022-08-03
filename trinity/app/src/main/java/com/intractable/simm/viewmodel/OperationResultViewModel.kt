package com.intractable.simm.viewmodel

import androidx.lifecycle.ViewModel
import javax.inject.Inject
import javax.inject.Named

class OperationResultViewModel @Inject constructor(

    @Named("outputMessageForNotification")outputMessageForNotification : String,
    @Named("NextButton")NextButton : String

) : ViewModel() {


}