package com.intractable.simm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.intractable.simm.gamelogic.Config
import com.intractable.simm.model.SessionModel

class SplashScreenViewModel: ViewModel() {


    val splashScreenImage: LiveData<SessionModel?>
        get() {
            val progressManager = Config.instance.progressManager
            return progressManager.getSessionModelFlow().asLiveData()


        }
    val splashScreenComment: LiveData<SessionModel?>
        get() {
            val progressManager = Config.instance.progressManager
            return progressManager.getSessionModelFlow().asLiveData()


        }

}