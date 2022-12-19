package com.intractable.simm.gamelogic
import android.util.Log
import com.intractable.simm.R
import com.intractable.simm.dataStore.StorageManager
import com.intractable.simm.model.*
import com.intractable.simm.utils.Plants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.newSingleThreadContext

class ProgressManagerTestImpl : IProgressManager {
    private val TAG = "ProgressManagerTestImpl"

    private var sessionNumber: Int = 0
    private var plantImage: Int = Plants.plantImages[1][0][0]

    private val sessionModels: ArrayList<SessionModel> = ArrayList<SessionModel>()
    private val fragments: ArrayList<String> = ArrayList()

    private var activityCount = 0
    private var plantStageAnimationFlow = MutableStateFlow<Pair<Int?,Int?>?>(null)
    private val progressDispatcher = newSingleThreadContext("progressDispatcher")

    override fun getOnboardingListFlow(): Flow<ArrayList<OnBoarding>> {
        return flow {
            emit(onBoardingModels)
        }.flowOn(progressDispatcher)

    }
    private val onBoardingModels: ArrayList<OnBoarding> = ArrayList<OnBoarding>()


    override fun initStorageManager(storageManager: StorageManager) {
        val appContext = Config.instance.applicationContext
        val bellIcon = appContext?.resources?.getIdentifier("bell_icon", "drawable", appContext.packageName) ?: 0
        val cameraIcon = appContext?.resources?.getIdentifier("camera_icon", "drawable", appContext.packageName) ?: 0
        val healthIcon = appContext?.resources?.getIdentifier("health_icon", "drawable", appContext.packageName) ?: 0
        val learnIcon = appContext?.resources?.getIdentifier("learn_icon", "drawable", appContext.packageName) ?: 0
        val photosIcon = appContext?.resources?.getIdentifier("photos_icon", "drawable", appContext.packageName) ?: 0
        val productivityIcon = appContext?.resources?.getIdentifier("productivity_icon", "drawable", appContext.packageName) ?: 0
        val settingsIcon = appContext?.resources?.getIdentifier("settings_icon", "drawable", appContext.packageName) ?: 0
        val touchIcon = appContext?.resources?.getIdentifier("touch_icon", "drawable", appContext.packageName) ?: 0
        val widgetsIcon = appContext?.resources?.getIdentifier("widgets_icon", "drawable", appContext.packageName) ?: 0

        var sessionModel: SessionModel
        sessionModel = SessionModel()
        sessionModel.lessonNumber = "Lesson X"
        sessionModel.title = "Touch Input"
        sessionModel.comment = "Learn about touch input in your phone!"
        sessionModel.hasLockIcon = false
        sessionModel.listIcon = bellIcon
        sessionModels.add(sessionModel)
        fragments.add("FragmentTrueOrFalse")

        //
        sessionModel = SessionModel()
        sessionModel.title = "Sound"
        sessionModel.comment = "Learn about sound in your phone!"
        sessionModel.hasLockIcon = false
        sessionModel.listIcon = cameraIcon
        sessionModels.add(sessionModel)
        fragments.add("FragmentStoryboard")
        //

        sessionModel = SessionModel()
        sessionModel.lessonNumber = "Lesson X"
        sessionModel = SessionModel()
        sessionModel.title = "QuickSettings"
        sessionModel.comment = "Learn about quick settings!"
        sessionModel.hasLockIcon = true
        sessionModel.listIcon = healthIcon
        sessionModels.add(sessionModel)
        fragments.add("FragmentMcq")

        sessionModel = SessionModel()
        sessionModel.title = "Drawing Input"
        sessionModel.comment = "Learn about drawing in your phone!"
        sessionModel.hasPlayIcon = true
        sessionModel.listIcon = learnIcon
        sessionModels.add(sessionModel)
        fragments.add("FragmentHtml")

        sessionModel = SessionModel()
        sessionModel.lessonNumber = "Lesson X"
        sessionModel.title = "Pinch and Zoom New"
        sessionModel.comment = "testing pinch and zoom"
        sessionModel.description = "Test your skills with input gestures"
        sessionModel.longDescription = "Change the size of what you view through gestures."
        sessionModel.hasComingSoonState = true
        sessionModel.listIcon = photosIcon
        sessionModels.add(sessionModel)
        fragments.add("FragmentHtml")

        sessionModel = SessionModel()
        sessionModel.lessonNumber = "Lesson X"
        sessionModel.title = "QuickSettings"
        sessionModel.comment = "Learn about quick settings!"
        sessionModel.hasLockIcon = true
        sessionModel.listIcon = productivityIcon
        sessionModels.add(sessionModel)
        fragments.add("FragmentMcq")

        sessionModel = SessionModel()
        sessionModel.lessonNumber = "Lesson X"
        sessionModel.title = "Right or Wrong"
        sessionModel.comment = "testing right or wrong"
        sessionModel.description = "Test your knowledge with this Right/Wrong screen"
        sessionModel.longDescription = "Long description of Right/Wrong screen"
        sessionModel.listIcon = settingsIcon
        sessionModels.add(sessionModel)
        fragments.add("RightOrWrongFragment")

        sessionModel = SessionModel()
        sessionModel.lessonNumber = "Lesson X"
        sessionModel.title = "Notifications"
        sessionModel.comment = "Learn about notifications!"
        sessionModel.description = "Introducing notifications"
        sessionModel.longDescription = "Notifications help you keep track of what's new"
        sessionModel.hasLockIcon = true
        sessionModel.listIcon = touchIcon
        sessionModels.add(sessionModel)
        fragments.add("FragmentOperation")

        sessionModel = SessionModel()
        sessionModel.lessonNumber = "Lesson X"
        sessionModel.title = "Notifications 2"
        sessionModel.comment = "Learn more about notifications!"
        sessionModel.description = "Explore various types of notifications"
        sessionModel.longDescription = "Let's play a game with notifications!"
        sessionModel.hasLockIcon = true
        sessionModel.listIcon = widgetsIcon
        sessionModels.add(sessionModel)
        fragments.add("FragmentNotification")


        sessionModel = SessionModel()
        sessionModel.lessonNumber = "Lesson X"
        sessionModel.title = "Quick Settings"
        sessionModel.comment = "Learn about Quick Settings!"
        sessionModel.description = "Introducing Quick Settings"
        sessionModel.longDescription = "Quick Settings give you easy access to a select group of settings."
        sessionModel.hasCheckMark = true
        sessionModel.listIcon = bellIcon
        sessionModels.add(sessionModel)
        fragments.add("FragmentQuickSettings")

        sessionModel = SessionModel()
        sessionModel.lessonNumber = "Lesson X"
        sessionModel.title = "Widgets"
        sessionModel.comment = "Learn about Widgets!"
        sessionModel.description = "Introducing Widgets"
        sessionModel.longDescription = "Widgets are a great way to get information at a glance from your favorite apps."
        sessionModel.hasCheckMark = true
        sessionModel.listIcon = bellIcon
        sessionModels.add(sessionModel)
        fragments.add("FragmentWidget")

        sessionModel = SessionModel()
        sessionModel.lessonNumber = "Lesson X"
        sessionModel.title = "Drawing Input"
        sessionModel.comment = "Learn about drawing in your phone!"
        sessionModel.hasPlayIcon = true
        sessionModel.listIcon = bellIcon
        sessionModels.add(sessionModel)
        fragments.add("FragmentHtml")

        sessionModel = SessionModel()
        sessionModel.lessonNumber = "Lesson X"
        sessionModel.title = "Touch Input"
        sessionModel.comment = "Learn about touch input in your phone!"
        sessionModel.hasLockIcon = true
        sessionModel.listIcon = bellIcon
        sessionModels.add(sessionModel)
        fragments.add("FragmentTrueOrFalse")

        sessionModel = SessionModel()
        sessionModel.lessonNumber = "Lesson X"
        sessionModel.title = "Sound"
        sessionModel.comment = "Learn about sound in your phone!"
        sessionModel.hasLockIcon = true
        sessionModel.listIcon = bellIcon
        sessionModels.add(sessionModel)
        fragments.add("FragmentStoryboard")

        sessionModel = SessionModel()
        sessionModel.lessonNumber = "Lesson X"
        sessionModel.title = "App Permissions"
        sessionModel.comment = "Learn about app permissions!"
        sessionModel.hasLockIcon = true
        sessionModel.listIcon = bellIcon
        sessionModels.add(sessionModel)
        fragments.add("FragmentPermissions")

        sessionModel = SessionModel()
        sessionModel.lessonNumber = "Lesson X"
        sessionModel.title = "QuickSettings"
        sessionModel.comment = "Learn about quick settings!"
        sessionModel.hasLockIcon = true
        sessionModel.listIcon = bellIcon
        sessionModels.add(sessionModel)
        fragments.add("FragmentMcq")

        sessionModel = SessionModel()
        sessionModel.lessonNumber = "Lesson X"
        sessionModel.title = "Coming soon"
        sessionModel.comment = "I am working hard to bring you new sessions!"
        sessionModel.isActive = false
        sessionModel.hasLockIcon = true
        sessionModel.listIcon = bellIcon
        sessionModels.add(sessionModel)

        val plantIcon = appContext?.resources?.getIdentifier("plant_stage3", "drawable", appContext.packageName) ?: 0
        for (i in 0..20) {
            plantCollection.add(PlantItem(plantIcon))
        }
    }

    override fun saveProgress(completionStatus:Boolean) {
        sessionNumber++
        activityCount = 0
        plantImage = Plants.plantImages[1][0][sessionNumber % 6]
        plantStageAnimationFlow.value = Pair(0,0)
    }

//    override fun saveProgress() {
//        sessionNumber++
//        activityCount = 0
//        plantImage = Plants.plantImages[1][0][sessionNumber % 6]
//        plantStageAnimationFlow.value = 0
//    }

    override fun cancelSession() {
        //stub
    }

    override fun updateState(state: String) {
        Log.i(TAG, "received state $state")
    }

    override fun getHintFlow(): Flow<String?> {
        return flow {
            emit("Hint text")
        }
    }

    override fun setSessionIndex(index: Int) {
        sessionNumber = index
    }

    override fun getSessionNumberFlow(): Flow<Int> {
        return flow {
            emit(sessionNumber)
        }
    }

    override fun getPlantImageFlow(): Flow<Int> {
        return flow {
            emit(plantImage)
        }
    }

    override fun getPlantCountFlow(): Flow<Int> {
        return flow {
            emit(sessionNumber/3)
        }
    }

    override fun getPlantCountWithTotalFlow(): Flow<Pair<Int, Int>> {
        return flow {
            emit(Pair(10, 30))
        }
    }

    override fun getPlantCountSparkleFlow(): Flow<Int?> {
        return flow {
            emit(0)
        }
    }

    override fun getPlantStageSparkleFlow(): Flow<Int?> {
        return flow {
            emit(0)
        }
    }

    override fun getSparkleFlow(): Flow<Pair<Int?, String?>?> {
        return MutableStateFlow(null)
    }

    override fun decrementHeartsCount() {
        if (heartsStateFlow.value > 0) {
            heartsStateFlow.value = heartsStateFlow.value - 1
        }
    }

    override fun getPlantStageAnimationFlow(): Flow<Pair<Int?,Int?>?> {
        return plantStageAnimationFlow
    }

    override fun getEndCelebrationAnimationFlow(): Flow<Int> {
        return flow {
            val appContext = Config.instance.applicationContext
            emit(appContext?.resources?.getIdentifier("simm_thumbsup", "raw", appContext.packageName) ?: 0)
        }
    }

    override fun finishSparklePlantCount() {
        //stub
    }

    override fun finishSparklePlantStage() {
        //stub
    }

    override fun finishSparkle() {
        //stub
    }

    override fun finishPlantStageAnimation() {
        plantStageAnimationFlow.value = null
    }

    override fun getPlantStageFlow(): Flow<Pair<Int, Int>> {
        return flow {
            emit(Pair((activityCount % 5) + 1, 5))
        }
    }

    override fun getPlantNameFlow(): Flow<String> {
        return flow {
            emit("Red Tulip")
        }
    }

    override fun getSessionModelFlow(): Flow<SessionModel?> {
        return flow {
            emit(sessionModels[sessionNumber])
        }
    }

    override fun getSimmCommentFlow(): Flow<String> {
        return flow {
            emit("Enjoy your learning sessions today!")
        }
    }

    override fun ChangeProgress() {
        //stub
    }

    override fun getSessionListFlow(): Flow<ArrayList<SessionModel>> {
        return flow {
            emit(sessionModels)
        }
    }

    val heartsStateFlow = MutableStateFlow<Int>(5)

    override fun getHeartsCountFlow(): Flow<Int> {
        return heartsStateFlow
    }

    val plantCollection = ArrayList<PlantItem>()
    override fun getPlantCollectionFlow(): Flow<ArrayList<PlantItem>> {
        return flow {
            emit(plantCollection)
        }
    }

    override fun getPlantStageComment(): Flow<String> {
        return flow {
            emit("Complete a few more sesssion to grow a full plant!")
        }
    }

    override fun getEndSessionText(): Flow<String> {
        TODO("Not yet implemented")
    }

    override fun getPlantCollectionComment(): Flow<String> {
        return flow {
            emit("Keep learning to discover new plants!")
        }
    }

    override fun getSettingsComment(): Flow<String> {
        return flow {
            emit("You can change the app settings in this screen.")
        }
    }

    override fun getSessionCompletionProgress(): Flow<Int> {
        return flow {
            emit(activityCount/3)
        }
    }

    override fun getFragmentNameFlow(): Flow<String> {
        return flow {
            emit(fragments[sessionNumber])
        }
    }

    override fun getSkipTextFlow(): Flow<String> {
        TODO("Not yet implemented")
    }

    override fun hasNextActivityInSession(increment:Boolean): Boolean {
        activityCount++
        return activityCount < 1
    }

    override fun getMCQData(): Flow<QuestionItem> {
        return flow {
            val questionItem = QuestionItem(
                "Question $activityCount",
                "Answer 1",
                "Answer 2",
                "Answer 3",
                "Answer 4",
                "Answer 1",
                "".toInt()
            )
            emit(questionItem)
        }


    }

    fun getNotificationScreenText(): Flow<ArrayList<String>> {
        return flow {
            val notificationScreenText = ArrayList<String>()
            notificationScreenText.add("About notifications 1")
            notificationScreenText.add("About notifications 2")
            notificationScreenText.add("About notifications 3")
            notificationScreenText.add("About notifications 4")
            notificationScreenText.add("About notifications 5")
            notificationScreenText.add("About notifications 6")
            notificationScreenText.add("About notifications 7")
            notificationScreenText.add("About notifications 8")
            emit(notificationScreenText)
        }
    }

    var notificationData = OperationData("About notification ", "",false,false, R.drawable.neutral_mouth_open,true,1)


    override fun getStoryboardItems(): Flow<ArrayList<StoryBoardItem>> {
        return flow {
            val storyboardItems = ArrayList<StoryBoardItem>()
            val appContext = Config.instance.applicationContext
            val gesturesPage1 = appContext?.resources?.getIdentifier("gestures_page1", "drawable", appContext.packageName) ?: 0
            val gesturesPage2 = appContext?.resources?.getIdentifier("gestures_page2", "drawable", appContext.packageName) ?: 0
            val gesturesPage3 = appContext?.resources?.getIdentifier("notification_one", "raw", appContext.packageName) ?: 0
            Log.d("testttt",gesturesPage3.toString())

            storyboardItems.add(StoryBoardItem("Storyboard 1", gesturesPage1))
            storyboardItems.add(StoryBoardItem("Storyboard 2", gesturesPage2))
            storyboardItems.add(StoryBoardItem("Storyboard 3", 0, gesturesPage3))
            emit(storyboardItems)
        }
    }

    override fun getSortingData(): Flow<TrueOrFalseModel> {
        return flow {
            val statements = ArrayList<Statement>()
            statements.add(Statement("Statement 1 True", "True"))
            statements.add(Statement("Statement 2 False", "False"))
            statements.add(Statement("Statement 3 True", "True"))
            val sortingData = TrueOrFalseModel("Sort these items in True and False buckets",
            statements, "True", "False")
            emit(sortingData)
        }
    }

    override fun getHtmlData(): Flow<HtmlData> {
        return flow {
            val drawingShapeText = HtmlData("Swipe along the rectangle edges to draw the full rectangle.", 0, 0)
            emit(drawingShapeText)
        }
    }

    override fun getComicData(): Flow<ComicData> {
        TODO("Not yet implemented")
    }
    override fun getOnboardingComicFlow(): Flow<ComicData>{
        TODO("Not yet implemented")
    }

    override fun storeUserName(inputName: String) {
        TODO("Not yet implemented")
    }

    override fun getUserName(): Flow<String> {
        TODO("Not yet implemented")
    }

    override fun checkAndUpdateAppIfNeeded(){
        TODO("Not yet implemented")
    }

    override fun setHighestSessionIndex() {
        TODO("Not yet implemented")
    }

    override fun getPinchAndZoomText(): Flow<ArrayList<String>> {
        return flow {
            val pinchAndZoomText = ArrayList<String>()
            pinchAndZoomText.add("Pinch out to magnify the Star.")
            emit(pinchAndZoomText)
        }
    }

    override fun getOperationState(): Flow<OperationData?> {
        TODO("Not yet implemented")
    }

    override fun nextOperationPage() {
        TODO("Not yet implemented")
    }

    override fun startOperationData() {
        TODO("Not yet implemented")
    }

    override fun getHintsTextFlow(): Flow<ArrayList<String>> {
        TODO("Not yet implemented")
    }

    override fun getOrientationState(): Flow<OperationData?> {
        TODO("Not yet implemented")
    }

    override fun nextOrientationPage() {
        TODO("Not yet implemented")
    }

    override fun startOrientationData() {
        TODO("Not yet implemented")
    }

    private var sessionSkipped = false
    override fun getLessonCompleteAnimationFlow(): Flow<Int> {
        return flow {
            if(sessionSkipped){
                emit(R.raw.cactus_blue_s5_end)
            }
            else{
                emit(R.raw.lesson_complete)
            }
        }.flowOn(progressDispatcher)
    }

    var notificationStateFlow = MutableStateFlow<OperationData>(OperationData("Notification 1", "Color 1", false, false,R.drawable.neutral_mouth_open,true,1))
    var notificationPage = 0
    var finishCalled = false

    var notification2Page = 0



    override fun getRightOrWrongData(): Flow<RightOrWrongItem> {
        return flow {
            val rightOrWrongItem = RightOrWrongItem("statement $activityCount", "Answer")
            emit(rightOrWrongItem)
        }
    }

    override fun getExplanationText(): Flow<String> {
        return flow {
            emit("Control your phone using a few simple gestures, like tap, touch and hold, swipe, scroll, and zoom.")
        }
    }

    override fun getFeedbackData(): Flow<InstructionData> {
        return flow {
            val feedbackData = InstructionData("Nice work!")
            emit(feedbackData)
        }
    }

    companion object {
        val instance = ProgressManagerTestImpl()
    }
}