package com.intractable.simm.viewmodel

import androidx.lifecycle.*
import com.intractable.simm.gamelogic.Config
import com.intractable.simm.model.SessionModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class HomeViewModel : ViewModel() {



    private var sizeOfList = MutableLiveData<Int>()


    val plantCountFlow: LiveData<Int>
    get() {
        val progressManager=Config.instance.progressManager
        return progressManager.getPlantCountFlow().asLiveData()
    }

    val heartCountFlow: LiveData<Int?>
        get() {
            val progressManager=Config.instance.progressManager
            return progressManager.getHeartsCountFlow().asLiveData()
        }

    val sessionCountFlow: LiveData<Int>
    get(){
        val progressManager=Config.instance.progressManager
        return progressManager.getSessionNumberFlow().asLiveData()
    }

    val sessionListFlow: LiveData<ArrayList<SessionModel>>
        get() {
            val progressManager  = Config.instance.progressManager
            return progressManager.getSessionListFlow().asLiveData()
        }

    val sessionModelFlow: LiveData<SessionModel?>
        get() {
            val progressManager = Config.instance.progressManager
            return progressManager.getSessionModelFlow().asLiveData()
        }

    val simmCommentFlow: LiveData<String>
        get() {
            val progressManager = Config.instance.progressManager
            return progressManager.getSimmCommentFlow().asLiveData()
        }

    val plantStageFlow: LiveData<Pair<Int, Int>>
        get(){
            val progressManager = Config.instance.progressManager
            return progressManager.getPlantStageFlow().asLiveData()
        }

    val plantStageSparkleFlow: LiveData<Int?>
        get(){
            val progressManager = Config.instance.progressManager
            return progressManager.getPlantStageSparkleFlow().asLiveData()
        }

    val plantCountSparkleFlow: LiveData<Int?>
        get(){
            val progressManager = Config.instance.progressManager
            return progressManager.getPlantCountSparkleFlow().asLiveData()
        }

    val sparkleFlow: LiveData<Pair<Int?, String?>?>
        get() {
            val progressManager = Config.instance.progressManager
            return progressManager.getSparkleFlow().asLiveData()
        }

    fun finishSparklePlantStage() {
        val progressManager = Config.instance.progressManager
        progressManager.finishSparklePlantStage()
    }

    fun finishSparklePlantCount()
    {
        val progressManager = Config.instance.progressManager
        progressManager.finishSparklePlantCount()
    }

    fun setSession(index: Int){
        val progressManager = Config.instance.progressManager
        progressManager.setSessionIndex(index)
    }

    fun getSessionModelLiveData(): LiveData<ArrayList<SessionModel>> {
        val sessionModelFlow = Config.instance.progressManager.getSessionListFlow()
        return sessionModelFlow.asLiveData()
    }

        fun generateLessons(arrayList: ArrayList<SessionModel>): Flow<ArrayList<SessionModel>> {
            val lessonList = Config.instance.progressManager.getSessionListFlow()
            return sessionListFlow.asFlow()
     }

    fun finishSparkle() {
        val progressManager = Config.instance.progressManager
        progressManager.finishSparkle()
    }

}