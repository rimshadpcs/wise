package com.intractable.simm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.intractable.simm.gamelogic.Config

class PlantStageViewModel: ViewModel() {

    val plantImageFlow: LiveData<Int>
        get(){
            val progressManager= Config.instance.progressManager
            return progressManager.getPlantImageFlow().asLiveData()
        }

    val plantStageFlow: LiveData<Pair<Int, Int>>
        get(){
            val progressManager = Config.instance.progressManager
            return progressManager.getPlantStageFlow().asLiveData()
        }

    val plantNameFlow: LiveData<String>
    get(){
        val progressManager = Config.instance.progressManager
        return progressManager.getPlantNameFlow().asLiveData()
    }

    val plantStageComment: LiveData<String>
        get() {
            val progressManager = Config.instance.progressManager
            return progressManager.getPlantStageComment().asLiveData()
        }

}