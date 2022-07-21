package com.example.simmone.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simmone.model.OnBoarding

class OnBoardingViewModel: ViewModel {

    var onBoardingMessage=""
    constructor(): super()
    constructor(onBoarding: OnBoarding): super(){
    }

    var mutableOnBoardingList = MutableLiveData<ArrayList<OnBoardingViewModel>>()
    var onBoardingList = ArrayList<OnBoardingViewModel>()

    var newMOnboardingList = MutableLiveData<ArrayList<OnBoarding>>()



    fun generateOnBoardingMessage(){
        val newOnboardList = ArrayList<OnBoarding>()

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

        newMOnboardingList.value = newOnboardList



    }
}