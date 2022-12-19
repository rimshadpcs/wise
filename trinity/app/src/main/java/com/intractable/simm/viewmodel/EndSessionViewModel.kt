package com.intractable.simm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.intractable.simm.gamelogic.Config

class EndSessionViewModel: ViewModel() {
    val progressManager = Config.instance.progressManager

    fun finishPlantStageAnimation() {
        progressManager.finishPlantStageAnimation()
    }


    val endSessionCelebration: LiveData<Int>
        get() {
            val progressManager = Config.instance.progressManager
            return progressManager.getEndCelebrationAnimationFlow().asLiveData()
        }
    val lessonCompleteAnimation: LiveData<Int>
        get() {
            val progressManager = Config.instance.progressManager
            return progressManager.getLessonCompleteAnimationFlow().asLiveData()
        }

    val plantStageAnimationFlow: LiveData<Pair<Int?,Int?>?>
        get() {
            return progressManager.getPlantStageAnimationFlow().asLiveData()
        }
    val plantStageComment: LiveData<String>
        get() {
            val progressManager = Config.instance.progressManager
            return progressManager.getPlantStageComment().asLiveData()
        }

    val endSessionText: LiveData<String>
    get(){
        return progressManager.getEndSessionText().asLiveData()
    }
}