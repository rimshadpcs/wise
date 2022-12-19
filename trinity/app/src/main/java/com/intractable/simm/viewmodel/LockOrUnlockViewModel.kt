package com.intractable.simm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.intractable.simm.gamelogic.Config
import com.intractable.simm.model.SessionModel

class LockOrUnlockViewModel : ViewModel() {
    var heartsLiveData = MutableLiveData<Int>(5)

    fun setSession(index: Int){
        val progressManager = Config.instance.progressManager
        progressManager.setSessionIndex(index)

    }

    fun setHighestSession(){
        val progressManager = Config.instance.progressManager
        progressManager.setHighestSessionIndex()

    }

    val sessionModelFlow: LiveData<SessionModel?>
        get() {
            val progressManager=Config.instance.progressManager
            return progressManager.getSessionModelFlow().asLiveData()
        }

    val heartFlow : LiveData<Int>
    get(){
        val progressManager = Config.instance.progressManager
        return progressManager.getHeartsCountFlow().asLiveData()
    }


}