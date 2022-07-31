package com.intractable.simm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.intractable.simm.model.OnBoarding
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.min

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

        val onBoardMessage1 = OnBoarding("My name is Simm. I am a Digital Activist.")
        val onBoardMessage1a = OnBoarding("My name is Simm. I am a Digital Activist.")
        val onBoardMessage1b = OnBoarding("I see you are here to learn about smartphones and digital technology.")
        val onBoardMessage1c = OnBoarding("I am happy to guide you on your learning journey!")
        val onBoardMessage2 = OnBoarding("The world is in crisis, digital technology is getting more complicated every day!")
        val onBoardMessage3 = OnBoarding("Educational books, websites, and videos just can't keep up with ever-changing technology.")
        val onBoardMessage4 = OnBoarding("But I see a world where everybody is empowered by Digital Technology.")
        val onBoardMessage5 = OnBoarding("The power to change your life is in your hands, literally with your smartphone.")
        val onBoardMessage6 = OnBoarding("Join the revolution, save the world!")


        newOnboardList.add(onBoardMessage1)
        newOnboardList.add(onBoardMessage1a)
        newOnboardList.add(onBoardMessage1b)
        newOnboardList.add(onBoardMessage1c)
        newOnboardList.add(onBoardMessage2)
        newOnboardList.add(onBoardMessage3)
        newOnboardList.add(onBoardMessage4)
        newOnboardList.add(onBoardMessage5)
        newOnboardList.add(onBoardMessage6)

        val delayTimes = longArrayOf(2000, 4000, 6000)
        viewModelScope.launch(Dispatchers.Main) {
            for(i in 1 until newOnboardList.size ){
                val delayTime = delayTimes[min(i - 1, delayTimes.size - 1)]
                delay(delayTime)
                newOnboardList2.add(newOnboardList[i])

            }
        }

        sizeOfMessageList.value = newOnboardList.size
        newMOnboardingList.value = newOnboardList2




    }
}