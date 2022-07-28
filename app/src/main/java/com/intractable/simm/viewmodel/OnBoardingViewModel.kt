package com.intractable.simm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.intractable.simm.model.OnBoarding
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OnBoardingViewModel: ViewModel {

    var onBoardingMessage=""
    constructor(): super()
    constructor(onBoarding: OnBoarding): super(){
    }

    var mutableOnBoardingList = MutableLiveData<ArrayList<OnBoardingViewModel>>()
    var onBoardingList = ArrayList<OnBoardingViewModel>()

    var newMOnboardingList = MutableLiveData<ArrayList<OnBoarding>>()
    var sizeOfMessageList = MutableLiveData<Int>()



    fun generateOnBoardingMessage(){
        val newOnboardList = ArrayList<OnBoarding>()
        val newOnboardList2 = ArrayList<OnBoarding>()

        val onBoardMessage1 = OnBoarding("Roses are red, violets are blue, poetry is hard. Avocado.")
        val onBoardMessage2 = OnBoarding("Roses are #FF0000, Violets are #0000FF, All my base belongs to you.")
        val onBoardMessage3 = OnBoarding("Roses are red, violets are blue, Whoa, déjà vu!")
        val onBoardMessage4 = OnBoarding("Roses are red, violets are too, I’m colorblind, what about you?")
        val onBoardMessage5 = OnBoarding("Roses are red, violets are blue, sheep go baah, and cows go moo.")
        val onBoardMessage6 = OnBoarding("Roses are red, violets are blue, these jokes are getting old, and so are you. ")


        newOnboardList.add(onBoardMessage1)
        newOnboardList.add(onBoardMessage2)
        newOnboardList.add(onBoardMessage3)
        newOnboardList.add(onBoardMessage4)
        newOnboardList.add(onBoardMessage5)
        newOnboardList.add(onBoardMessage6)

        viewModelScope.launch(Dispatchers.Main) {
            for(i in 1 until newOnboardList.size ){
                delay(2000)
                newOnboardList2.add(newOnboardList[i])

            }
        }

        sizeOfMessageList.value = newOnboardList.size
        newMOnboardingList.value = newOnboardList2




    }
}