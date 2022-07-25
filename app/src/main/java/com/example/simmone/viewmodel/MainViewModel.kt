package com.example.simmone.viewmodel
import androidx.lifecycle.ViewModel
import javax.inject.Inject
import javax.inject.Named

class MainViewModel @Inject constructor(
    @Named("continueButton") continueButton: String,
    @Named("bubbleSampleButton")bubbleSampleButton: String
): ViewModel(){

}


