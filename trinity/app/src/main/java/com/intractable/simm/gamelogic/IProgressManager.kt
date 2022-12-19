package com.intractable.simm.gamelogic

import com.intractable.simm.dataStore.StorageManager
import com.intractable.simm.model.*
import kotlinx.coroutines.flow.Flow

interface IProgressManager {

    fun getOnboardingListFlow(): Flow<ArrayList<OnBoarding>>

    fun initStorageManager(storageManager: StorageManager)

    fun saveProgress(completionStatus: Boolean = true)

    fun cancelSession()

    fun updateState(state: String)

    fun getHintFlow(): Flow<String?>

    fun setSessionIndex(index: Int)

    fun getSessionNumberFlow(): Flow<Int>

    fun getPlantImageFlow(): Flow<Int>

    fun getPlantCountFlow(): Flow<Int>

    fun getPlantCountWithTotalFlow(): Flow<Pair<Int, Int>>

    fun getPlantCountSparkleFlow(): Flow<Int?>

    fun getPlantStageSparkleFlow(): Flow<Int?>

    fun getSparkleFlow(): Flow<Pair<Int?, String?>?>

    fun getPlantStageAnimationFlow(): Flow<Pair<Int?,Int?>?>

    fun getEndCelebrationAnimationFlow(): Flow<Int>

    fun finishSparklePlantCount()

    fun finishSparklePlantStage()

    fun finishSparkle()

    fun finishPlantStageAnimation()

    // Provides stage count (starting with 1) and total number of stages. Examples (1,5), (3,5)
    fun getPlantStageFlow(): Flow<Pair<Int, Int>>

    fun getPlantNameFlow(): Flow<String>

    fun getSessionModelFlow(): Flow<SessionModel?>

    fun getSimmCommentFlow(): Flow<String>

    fun ChangeProgress()

    fun getSessionListFlow(): Flow<ArrayList<SessionModel>>


    fun getHeartsCountFlow(): Flow<Int>

    fun getPlantCollectionFlow(): Flow<ArrayList<PlantItem>>

    fun getPlantStageComment(): Flow<String>

    fun getEndSessionText(): Flow<String>

    fun getPlantCollectionComment(): Flow<String>

    fun getSettingsComment(): Flow<String>

    // Session progress

    fun getSessionCompletionProgress(): Flow<Int>

    fun getFragmentNameFlow(): Flow<String>

    fun getSkipTextFlow() : Flow<String>

    fun hasNextActivityInSession(increment:Boolean): Boolean

    // Provides data for one MCQ

    fun getMCQData(): Flow<QuestionItem>

    // Provides data for right or wrong


    fun getStoryboardItems(): Flow<ArrayList<StoryBoardItem>>

    fun getSortingData(): Flow<TrueOrFalseModel>


    fun getHtmlData(): Flow<HtmlData>
    fun getComicData(): Flow<ComicData>

    fun getPinchAndZoomText(): Flow<ArrayList<String>>

    //operation session
    fun getOperationState(): Flow<OperationData?>
    fun nextOperationPage()
    fun startOperationData()

    fun getHintsTextFlow(): Flow<ArrayList<String>>

    //orientation session
    fun getOrientationState(): Flow<OperationData?>
    fun nextOrientationPage()
    fun startOrientationData()

    fun getRightOrWrongData(): Flow<RightOrWrongItem>
    fun getExplanationText(): Flow<String>
    fun getFeedbackData(): Flow<InstructionData>
    fun decrementHeartsCount()

    fun getOnboardingComicFlow(): Flow<ComicData>
    fun storeUserName(inputName: String)
    fun getUserName(): Flow<String>
    fun checkAndUpdateAppIfNeeded()
    fun setHighestSessionIndex()
    fun getLessonCompleteAnimationFlow(): Flow<Int>


}

