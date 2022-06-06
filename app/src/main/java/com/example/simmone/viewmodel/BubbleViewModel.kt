package com.example.simmone.viewmodel
import androidx.lifecycle.ViewModel
import javax.inject.Inject
import javax.inject.Named

class BubbleViewModel @Inject constructor(
    @Named("bubbleSampleMessage") bubbleSampleMessage: String,
    @Named("bubbleSampleButton")bubbleSampleButton: String

) : ViewModel() {
    //val startButton : String = ("Continue")


}