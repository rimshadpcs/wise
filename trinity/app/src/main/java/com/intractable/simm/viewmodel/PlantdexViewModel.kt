package com.intractable.simm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.intractable.simm.gamelogic.Config
import com.intractable.simm.model.PlantItem

class PlantdexViewModel: ViewModel() {
    var plantList = MutableLiveData<ArrayList<PlantItem>>()



    val plantCountFlow: LiveData<ArrayList<PlantItem>>
        get() {
            val progressManager = Config.instance.progressManager
            return progressManager.getPlantCollectionFlow().asLiveData()

        }

    val plantCollectionCommentFlow: LiveData<String>
        get() {
            val progressManager = Config.instance.progressManager
            return progressManager.getPlantCollectionComment().asLiveData()
        }
    val plantTotalCount: LiveData<Pair<Int,Int>>
        get() {
            val progressManager = Config.instance.progressManager
            return progressManager.getPlantCountWithTotalFlow().asLiveData()
        }

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