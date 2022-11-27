package com.intractable.simm.viewmodel

import android.os.Bundle
import android.util.Log
import androidx.compose.Observe
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.intractable.simm.gamelogic.Config
import com.intractable.simm.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.collections.ArrayList

class SessionViewModel:ViewModel() {
    private val tag = "SessionViewModel"
    val progressManager = Config.instance.progressManager
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    var progressLiveData = MutableLiveData(0)
    fun getProgressValue():LiveData<Int> = progressLiveData
    var heartsLiveData = MutableLiveData(5)

    var shouldSparkle = true

    init {
        progressManager.getHeartsCountFlow().asLiveData().observeForever {
            heartsLiveData.value = it
        }
    }
    val progress: LiveData<Int>
        get() {
            return progressManager.getSessionCompletionProgress().asLiveData()
        }

    val plantStageAnimationFlow: LiveData<Pair<Int?,Int?>?>
        get() {
            return progressManager.getPlantStageAnimationFlow().asLiveData()
        }

    var skipTextFlow = ""

    var cameraPermissionDenied = false
    init {
        Config.instance.storageManager?.cameraPermissionDenied!!.asLiveData().observeForever {
            cameraPermissionDenied = it
        }
    }

    //FragmentMcq
    lateinit var questionItem: QuestionItem
    var currentQuestion = 0
    var fragmentLiveData = MutableLiveData("")
    var page = 0
    val mcqData: LiveData<QuestionItem>
        get() {
            return progressManager.getMCQData().asLiveData()
        }

    //Fragment RightOrWrong
    lateinit var rightOrWrongItem: RightOrWrongItem
    var currentStatement =0

    val rightOrWrongData: LiveData<RightOrWrongItem>
        get() {
            val progressManager = Config.instance.progressManager
            return progressManager.getRightOrWrongData().asLiveData()
        }

    //FragmentTrueOrFalse
    var answer = ""
    val sortingData: LiveData<TrueOrFalseModel>
        get() {
            return progressManager.getSortingData().asLiveData()
        }

    //FragmentOperation

    var operationDataLiveData:MutableLiveData<OperationData> = MutableLiveData(null)
    var operationPage = 0
    init {
        progressManager.getOperationState().asLiveData().observeForever {
            if (it != null) {

                operationDataLiveData.value = OperationData(it.text,it.color,it.shouldDoAction,it.shouldGoToNextActivity,it.img,it.hasCountDown,it.sessionType,it.textError)
            }
        }
    }

    //FragmentOrientation
    var orientationLiveData:MutableLiveData<OperationData> = MutableLiveData(null)
    var orientationPage = 0
    init {
        progressManager.getOrientationState().asLiveData().observeForever {
            if (it != null) {
                Log.e("Data",it.text)
                orientationLiveData.value = OperationData(it.text,it.color,it.shouldDoAction,it.shouldGoToNextActivity,it.img,it.hasCountDown)
            }
        }
    }

    //FragmentStoryboard
    var storyboardItems: ArrayList<StoryBoardItem> = ArrayList()
    val storyboardItemsData: LiveData<ArrayList<StoryBoardItem>>
        get() {
            return progressManager.getStoryboardItems().asLiveData()
        }



    //FragmentPinchAndZoom
    val pinchAndZoomText: LiveData<ArrayList<String>>
        get() {
            return progressManager.getPinchAndZoomText().asLiveData()
        }

    //FragmentHtml
    val htmlData: LiveData<HtmlData>
        get() {
            return progressManager.getHtmlData().asLiveData()
        }

    //FragmentComic
    val comicData: LiveData<ComicData>
        get() {
            return progressManager.getComicData().asLiveData()
        }

    val userInputName: LiveData<String>
        get(){
            return progressManager.getUserName().asLiveData()
        }

    val hintFlow: LiveData<String?>
        get() {
            return progressManager.getHintFlow().asLiveData()
        }

    //FragmentExplanation
    val explanationText: LiveData<String>
    get() {
        return progressManager.getExplanationText().asLiveData()
    }

    //FragmentFeedback
    val instructionData: LiveData<InstructionData>
        get() {
            return progressManager.getFeedbackData().asLiveData()
        }

    //fragment call
    private val _onlyPhoneNumberFlow = MutableStateFlow("Good job \n" +
            "\n" +
            "Look like that last call was with ")
    val onlyPhoneFlow = _onlyPhoneNumberFlow.asStateFlow()

    fun getFragment():LiveData<String> = fragmentLiveData


    val eventlivedata = MutableLiveData(EVENT_NONE)
    fun getEvent(): LiveData<Int?> = eventlivedata

    fun checkForNextQuestion(completed:Boolean){

        //TODO: Use ProgressManager.hasNextActivityInSession
        // load next question if any
        var increment = true
        if (fragmentLiveData.value.equals("FragmentFeedback"))
            increment = false

        if (progressManager.hasNextActivityInSession(increment)) {
            Log.e("value","sd")
            page++
            eventlivedata.value = EVENT_NEXT_PAGE
        }
        else{
            progressManager.saveProgress(completed)
        }
    }

    fun nextOperationPage() {
        Log.i(tag, "nextQuickSettingsPage() called")
        operationPage++
        progressManager.nextOperationPage()
    }

    fun startOperationData() {
        progressManager.startOperationData()
    }

    fun nextOrientationPage() {
        Log.i(tag, "nextQuickSettingsPage() called")
        orientationPage++
        progressManager.nextOrientationPage()
    }

    fun startOrientationData() {
        progressManager.startOrientationData()
    }

    fun changeProgress(){
        progressManager.ChangeProgress()
    }

    fun checkCameraPermission(){
        eventlivedata.value = EVENT_CHECK_CAMERA_PERMISSION
        eventlivedata.value = EVENT_NONE
    }

    fun showSkipButton(){
        eventlivedata.value = EVENT_SHOW_SKIP_BUTTON
        eventlivedata.value = EVENT_NONE
    }


    fun checkForNextStatement(){

        val progressManager = Config.instance.progressManager
        //TODO: Use ProgressManager.hasNextActivityInSession
        // load next question if any
        if (progressManager.hasNextActivityInSession(true)) {
            Log.e("next","sd")
            page++
            eventlivedata.value = EVENT_NEXT_PAGE
        }
        else{
            eventlivedata.value = EVENT_FINISH_SESSION
        }
    }

    fun decrementHeartsCount() {

        progressManager.decrementHeartsCount()
        firebaseAnalytics = Firebase.analytics
        val params = Bundle()
        params.putString("heart_broken", "")
        firebaseAnalytics.logEvent("HEART_COUNT_REDUCED",params)

    }

    fun updateState(state: String) {
        progressManager.updateState(state)
    }

    fun cancelSession() {
        progressManager.cancelSession()
    }

    companion object sessionFlags{
        //flags
        var launchedPermissionScreen = false

        //requestcodes
        const val REQUEST_CODE_PERMISSIONS = 10
        const val REQUEST_CODE_PERMISSIONS_NEARBY = 11

        //FLAGS
        var progressSession = 0

        const val EVENT_NONE = 1001
        const val EVENT_SHOW_RIGHT_BOTTOMSHEET = 1002
        const val EVENT_SHOW_WRONG_BOTTOMSHEET = 1003
        const val EVENT_NEXT_PAGE = 1004
        const val EVENT_FINISH_SESSION = 1005
        const val EVENT_SHOW_RIGHT_BOTTOMSHEET_STATEMENT = 1007
        const val EVENT_SHOW_WRONG_BOTTOMSHEET_STATEMENT = 1008
        const val EVENT_CHECK_CAMERA_PERMISSION = 1009
        const val EVENT_START_CAMERA = 1010
        const val EVENT_CAPTURE_IMAGE = 1011
        const val EVENT_SHOW_SKIP_BUTTON = 1012
        const val EVENT_SHOW_NOTIFICATION_BAR = 1013
    }
}
