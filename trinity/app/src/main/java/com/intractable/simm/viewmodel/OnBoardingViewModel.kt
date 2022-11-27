package com.intractable.simm.viewmodel

import androidx.lifecycle.*
import com.intractable.simm.model.OnBoarding
import com.intractable.simm.gamelogic.Config
import com.intractable.simm.model.ComicData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

class OnBoardingViewModel: ViewModel {

    var onBoardingMessage=""
    constructor(): super()
    constructor(onBoarding: OnBoarding): super(){
    }

    val progressManager  = Config.instance.progressManager
    val storageManager = Config.instance.storageManager

    var mutableOnBoardingList = MutableLiveData<ArrayList<OnBoardingViewModel>>()
    var onBoardingList = ArrayList<OnBoardingViewModel>()

    var newMOnboardingList = MutableLiveData<ArrayList<OnBoarding>>()
    var sizeOfMessageList = MutableLiveData<Int>()



    // Comic
    val comicData: LiveData<ComicData>
        get() {
            return progressManager.getOnboardingComicFlow().asLiveData()
        }

    val userInputName: LiveData<String>
    get(){
        return progressManager.getUserName().asLiveData()
    }
}