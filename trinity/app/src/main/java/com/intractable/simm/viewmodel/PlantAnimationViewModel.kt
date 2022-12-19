package com.intractable.simm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.intractable.simm.gamelogic.Config

class PlantAnimationViewModel: ViewModel() {
    val progressManager = Config.instance.progressManager



    val plantStageAnimationFlow: LiveData<Pair<Int?,Int?>?>
        get() {
            return progressManager.getPlantStageAnimationFlow().asLiveData()
        }
    fun finishPlantStageAnimation() {
        progressManager.finishPlantStageAnimation()
    }

}