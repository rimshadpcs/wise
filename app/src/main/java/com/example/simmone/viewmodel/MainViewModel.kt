package com.example.simmone.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.simmone.dataStore.GoldManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class MainViewModel @Inject constructor(
    @Named("continueButton") continueButton: String,
    @Named("bubbleSampleButton")bubbleSampleButton: String
): ViewModel(){

}


