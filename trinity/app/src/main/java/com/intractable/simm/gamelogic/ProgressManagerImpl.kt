package com.intractable.simm.gamelogic

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.util.Log
import android.widget.RemoteViews
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.intractable.simm.R
import com.intractable.simm.dataStore.StorageManager
import com.intractable.simm.model.*
import com.intractable.simm.utils.DateTimeFunctions
import com.intractable.simm.utils.Plants
import com.intractable.simm.view.widgets.PlantWidget
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.Integer.min
import java.nio.charset.Charset
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class ProgressManagerImpl : IProgressManager {
    val TAG = "ProgressManagerImpl"
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    @OptIn(DelicateCoroutinesApi::class)
    private val progressDispatcher = newSingleThreadContext("progressDispatcher")
    var storageManager: StorageManager? = null


    private val onBoardingModels: ArrayList<OnBoarding> = ArrayList<OnBoarding>()
    private lateinit var onBoardingComic: ComicData

    init {

        val onBoardingModel01 = OnBoarding()
        onBoardingModel01.onBoardingMessage = "0"
        onBoardingModel01.duration = 0
        onBoardingModel01.simmImage = R.drawable.simm_wave_onboarding
        onBoardingModels.add(onBoardingModel01)

        val onBoardingModel1 = OnBoarding()
        onBoardingModel1.onBoardingMessage = "My name is Simm. I am a Digital Activist."
        onBoardingModel1.duration = 2000
        onBoardingModel1.simmImage = R.drawable.simm_excited_feedback
        onBoardingModels.add(onBoardingModel1)

        val onBoardingModel2 = OnBoarding()
        onBoardingModel2.onBoardingMessage =
            "I see you are here to learn about smartphones and digital technology."
        onBoardingModel2.duration = 4000
        onBoardingModel2.simmImage = R.drawable.simm_excited_feedback
        onBoardingModels.add(onBoardingModel2)

        val onBoardingModel3 = OnBoarding()
        onBoardingModel3.onBoardingMessage = "I am happy to guide you on your learning journey!"
        onBoardingModel3.duration = 6000
        onBoardingModel3.simmImage = R.drawable.neutral_mouth_closed
        onBoardingModels.add(onBoardingModel3)

        val onBoardingModel4 = OnBoarding()
        onBoardingModel4.onBoardingMessage =
            "The world is in crisis, digital technology is getting more complicated every day!"
        onBoardingModel4.duration = 2000
        onBoardingModel4.simmImage = R.drawable.neutral_mouth_closed
        onBoardingModels.add(onBoardingModel4)

        val onBoardingModel5 = OnBoarding()
        onBoardingModel5.onBoardingMessage =
            "Educational books, websites, and videos just can't keep up with ever-changing technology."
        onBoardingModel5.duration = 2000
        onBoardingModel5.simmImage = R.drawable.neutral_mouth_open
        onBoardingModels.add(onBoardingModel5)

        val onBoardingModel6 = OnBoarding()
        onBoardingModel6.onBoardingMessage =
            "But I see a world where everybody is empowered by Digital Technology."
        onBoardingModel6.duration = 2000
        onBoardingModel6.simmImage = R.drawable.simm_excited_feedback
        onBoardingModels.add(onBoardingModel6)

        val onBoardingModel7 = OnBoarding()
        onBoardingModel7.onBoardingMessage =
            "The power to change your life is in your hands, literally with your smartphone."
        onBoardingModel7.duration = 2000
        onBoardingModel7.simmImage = R.drawable.simm_encouraging
        onBoardingModels.add(onBoardingModel7)

        val onBoardingModel8 = OnBoarding()
        onBoardingModel8.onBoardingMessage = "Join the revolution, save the world!"
        onBoardingModel8.duration = 2000
        onBoardingModel8.simmImage = R.drawable.simm_excited_feedback
        onBoardingModels.add(onBoardingModel8)
    }

    override fun getOnboardingListFlow(): Flow<ArrayList<OnBoarding>> {
        return flow {
            emit(onBoardingModels)
        }.flowOn(progressDispatcher)

    }

    override fun getOnboardingComicFlow(): Flow<ComicData> {
        return flow {
            try {
                // Comic onboarding
                val jsonString = loadJSONFromAsset(Config.instance.applicationContext!!, "comic-onboarding")
                val jsonObject = JSONObject(jsonString)
                try {
                    val parameters = jsonObject.getJSONObject("parameters")
                    val titleName = parameters.getString("title")
                    val sceneOption = parameters.getString("sceneOption").toInt()
                    val sessionOption = parameters.getString("sessionOption").toInt()
                    val comicdetails: MutableList<ComicDetails> = mutableListOf()
                    val comicDetailsJson = parameters.getJSONArray("comic")
                    for (i in 0 until comicDetailsJson.length()) {
                        val comicPage = ComicDetails("", mutableListOf())
                        val pageJson = comicDetailsJson.getJSONObject(i)
                        comicPage.background = pageJson.getString("background")
                        val speechBubblesJson = pageJson.getJSONArray("speechBubbles")
                        for (j in 0 until speechBubblesJson.length()) {
                            val oneBubbleJson = speechBubblesJson.getJSONObject(j)
                            val bubbleDisplayText = oneBubbleJson.getString("displayText")
                            val bubbleFontSize = oneBubbleJson.getString("fontSize")
                            val bubbleTextPosition = oneBubbleJson.getString("textPosition")
                            val bubbleTextBoxSize = oneBubbleJson.getString("textBoxSize")
                            val bubbleTextAlignment = oneBubbleJson.getString("textAlignment")
                            val bubbleImage = oneBubbleJson.getString("bubbleImage")
                            val bubblePosition = oneBubbleJson.getString("bubblePosition")
                            val bubbleSize = oneBubbleJson.getString("bubbleSize")
                            val oneSpeechBubble = ComicSpeechBubble(
                                bubbleDisplayText,
                                bubbleFontSize,
                                bubbleTextPosition,
                                bubbleTextBoxSize,
                                bubbleTextAlignment,
                                bubbleImage,
                                bubblePosition,
                                bubbleSize,
                                i.toString()
                            )
                            comicPage.comicSpeechBubbles.add(oneSpeechBubble)
                        }
                        comicdetails.add(comicPage)
                    }
                    onBoardingComic = ComicData(titleName, sceneOption, sessionOption, comicdetails)
                }
                catch (e: Exception){
                    Log.e(TAG, "unexpected Comic Data in onboarding")
                    throw e
                }
                emit(onBoardingComic)
            } catch (e: Exception) {
                Log.e(TAG, "unexpected Comic Data data: $onBoardingComic")
                throw e
            }
        }.flowOn(progressDispatcher)
    }


    private val sessionModels: ArrayList<SessionModel> = ArrayList<SessionModel>()
    private val sessionActivityData: ArrayList<ArrayList<LearningActivityModel>> = ArrayList()
    private val hintsDictionary: HashMap<String, String> = HashMap()
    private var highestSessionIndex = 0
    private var sessionNumber = 0
    private var activityCount = 0
    private var progressActivityCount = 0
    private val sparklePlantCountFlow: MutableStateFlow<Int?> = MutableStateFlow(null)
    private val sparklePlantStageFlow: MutableStateFlow<Int?> = MutableStateFlow(null)
    private val sparkleFlow: MutableStateFlow<Pair<Int?, String?>?> = MutableStateFlow(null)
    private val plantStageAnimationFlow: MutableStateFlow<Pair<Int?, Int?>?> = MutableStateFlow(null)

    private val fragments: ArrayList<String> = ArrayList()
    private var sessionCompletionStatus: String = ""


    fun init_bak() {
        // For testing, change this function to init block.
        var sessionModel = SessionModel()
        sessionModel.title = "Touch Input"
        sessionModel.comment = "Learn about touch input in your phone!"
        sessionModels.add(sessionModel)
        fragments.add("FragmentStoryboard")
        sessionModel = SessionModel()
        sessionModel.title = "Sound"
        sessionModel.comment = "Learn about sound in your phone!"
        sessionModels.add(sessionModel)
        fragments.add("FragmentStoryboard")
        sessionModel = SessionModel()
        sessionModel.title = "App Permissions"
        sessionModel.comment = "Learn about app permissions!"
        sessionModels.add(sessionModel)
        fragments.add("FragmentStoryboard")
        sessionModel = SessionModel()
        sessionModel.title = "Coming soon"
        sessionModel.comment = "I am working hard to bring you new sessions!"
        sessionModel.isActive = false
        sessionModels.add(sessionModel)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun initStorageManager(storageManager: StorageManager) {
        this.storageManager = storageManager
        GlobalScope.launch(progressDispatcher) {
            sessionCompletionStatus = storageManager.getSessionStatus()?:""
            sessionNumber = 0
            highestSessionIndex = storageManager.getSessionNumber() ?: 0
            loadJsonData()
            updatePlantStageAndCollection()
            sparkleFlow.value = Pair(
                null,
                sessionModels[min(highestSessionIndex, sessionModels.size - 1)].comment
                ,
            )
            heartsStateFlow.value = storageManager.getHearts() ?: 5

            prevPlantStage = storageManager.getPlantGrowth()?:0
        }
    }

    suspend fun getInitializedStorageManager(): StorageManager? = withContext(progressDispatcher) {
        this@ProgressManagerImpl.storageManager
    }

    private fun loadJsonData() {
        loadSession(Config.instance.applicationContext!!)
    }

    private fun loadJSONFromAsset(context: Context, quizFile: String): String {
        Log.d(TAG, "Quizfile: $quizFile")
        val charset: Charset = Charsets.UTF_8
        var json = ""
        try {
            val `is` = context.assets.open(quizFile + ".json")
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            json = String(buffer, charset)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return json
    }

    fun loadSession(context: Context) {
        Log.i(TAG, "Loading session description from json")
        val jsonStr = loadSessionFromAsset(context)

        try {
            val jsonObject = JSONObject(jsonStr)
            val sessionList = jsonObject.getJSONArray("sessionList")

            var lessonCount = 0
            var comicCount = 0
            var gameCount = 0
            for (i in 0 until sessionList.length()) {
                val question = sessionList.getJSONObject(i)
                val sessionId = question.getString("sessionId")
                val title = question.getString("title")
                Log.d("sessionId", sessionId)
                val activityList = question.getJSONArray("activityList")
                var sessionModel = SessionModel()
                sessionModel.sessionId = sessionId
                sessionModel.title = title
                sessionModel.comment = question.getString("comment")
                sessionModel.activityList = activityList.toArrayList()

                if (question.has("isActive") && "false".equals(question.getString("isActive"))) {
                    sessionModel.isActive = false
                }

                if(i < sessionCompletionStatus.length){
                    when(sessionCompletionStatus[i]){
                        '1' -> sessionModel.completionStatus = StorageManager.SessionStatus.Done
                        '2' -> sessionModel.completionStatus = StorageManager.SessionStatus.AttemptedIncomplete
                        else -> sessionModel.completionStatus = StorageManager.SessionStatus.NotDone
                    }
                }
                if (i < highestSessionIndex) {
                    sessionModel.hasCheckMark = true
                } else if (i == highestSessionIndex) {
                    sessionModel.hasPlayIcon = true
                } else {
                    sessionModel.hasLockIcon = true
                }

                if (question.has("iconImg")) {
                    val iconName = question.getString("iconImg")
                    val appContext = Config.instance.applicationContext!!
                    sessionModel.listIcon = appContext.resources.getIdentifier(
                        iconName,
                        "drawable",
                        appContext.packageName
                    )
                }

                if (question.has("endAnim")) {
                    val animName = question.getString("endAnim")
                    val appContext = Config.instance.applicationContext!!
                    sessionModel.endAnim =
                        appContext.resources.getIdentifier(animName, "raw", appContext.packageName)
                }

                if (question.has("titleAnimation")) {
                    val titleAnimation = question.getString("titleAnimation")
                    val appContext = Config.instance.applicationContext!!
                    sessionModel.titleAnimation = appContext.resources.getIdentifier(
                        titleAnimation,
                        "raw",
                        appContext.packageName
                    )
                }

                if (question.has("description")) {
                    sessionModel.description = question.getString("description")
                } else {
                    sessionModel.description = ""
                }

                if (question.has("longDescription")) {
                    sessionModel.longDescription = question.getString("longDescription")
                }

                if (question.has("expandable")) {
                    sessionModel.expandable = question.getBoolean("expandable")
                }

                if (question.has("hasComingSoonState")) {
                    sessionModel.hasComingSoonState =
                        question.getString("hasComingSoonState").equals("true")
                }

                if (question.has("splashImage")) {
                    val splashImage = question.getString("splashImage")
                    val appContext = Config.instance.applicationContext!!
                    sessionModel.splashScreenImage = appContext.resources.getIdentifier(
                        splashImage,
                        "drawable",
                        appContext.packageName
                    )
                }

                if (question.has("splashComment")) {
                    sessionModel.splashScreenComment = question.getString("splashComment")
                }

                if (question.has("hasWidgetReveal")) {
                    sessionModel.hasWidgetReveal =
                        question.getString("hasWidgetReveal").equals("true")
                }

                if (question.has("isComic")) {
                    comicCount++
                    sessionModel.lessonNumber = "Story $comicCount"

                    sessionModel.isComic = question.getString("isComic").equals("true")
                } else if(question.has("isGame")) {
                    gameCount++
                    sessionModel.lessonNumber = "Game $gameCount"

                    sessionModel.isGame = question.getString("isGame").equals("true")
                } else{
                    lessonCount++
                    sessionModel.lessonNumber = "Lesson $lessonCount"
                }

                if (question.has("backgroundImg")) {
                    val imgName = question.getString("backgroundImg")
                    val appContext = Config.instance.applicationContext!!
                    sessionModel.backgroundImg = appContext.resources.getIdentifier(
                        imgName,
                        "drawable",
                        appContext.packageName
                    )
                    sessionModel.hasBackgroundImg = true
                }
                if (question.has("lowerBackgroundImg")) {
                    val imgName = question.getString("lowerBackgroundImg")
                    val appContext = Config.instance.applicationContext!!
                    sessionModel.lowerBackgroundImg = appContext.resources.getIdentifier(
                        imgName,
                        "drawable",
                        appContext.packageName
                    )
                    sessionModel.hasLowerBackgroundImg = true
                }
                if (question.has("largeBackgroundImg")) {
                    val imgName = question.getString("largeBackgroundImg")
                    val appContext = Config.instance.applicationContext!!
                    sessionModel.largeBackgroundImg = appContext.resources.getIdentifier(
                        imgName,
                        "drawable",
                        appContext.packageName
                    )
                    sessionModel.hasLargeBackgroundImg = true
                }
                if(question.has("lockPageCharacter")){
                    sessionModel.lockPageCharacter = question.getString("lockPageCharacter")
                }

                sessionModels.add(sessionModel)
                val activityInfo = ArrayList<LearningActivityModel>()
                for (activityFileName in sessionModel.activityList!!) {
                    val activityJson = loadJSONFromAsset(context, activityFileName)
                    updateActivityData(activityInfo, activityJson)
                    Log.d("sessions", activityJson)
                }
                sessionActivityData.add(activityInfo)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun updateActivityData(
        activityInfo: ArrayList<LearningActivityModel>,
        activityJson: String
    ) {
        try {
            val jsonObject = JSONObject(activityJson)
            val fragment = jsonObject.getString("fragmentClassName")
            var skipText = ""
            if (jsonObject.has("skipText"))
                skipText = jsonObject.getString("skipText")
            Log.d(TAG, "fragment: $fragment")
            if (fragment.equals("FragmentMcq")) {

                val questions = jsonObject.getJSONObject("parameters")
                var answerId = Int.MAX_VALUE
                if (questions.has("answer_id")) {
                    answerId = questions.getInt("answer_id")
                    val questionString = questions.getString("question")
                    val choice1String = questions.getString("choice1")
                    val choice2String = questions.getString("choice2")
                    val choice3String = questions.getString("choice3")
                    val choice4String = questions.getString("choice4")
                    val answer = questions.getString("answer")

                    val questionItem =
                        QuestionItem(
                            questionString,
                            choice1String,
                            choice2String,
                            choice3String,
                            choice4String,
                            answer,
                            answerId
                        )

                    val learningActivityModel = LearningActivityModel(fragment, questionItem)
                    activityInfo.add(learningActivityModel)
                }
                else if (!questions.has("answer_id")){
                    val questionString = questions.getString("question")
                    val choice1String = questions.getString("choice1")
                    val choice2String = questions.getString("choice2")
                    val choice3String = questions.getString("choice3")
                    val choice4String = questions.getString("choice4")
                    val answer = questions.getString("answer")
                    val questionItem =
                        QuestionItem(
                            questionString,
                            choice1String,
                            choice2String,
                            choice3String,
                            choice4String,
                            answer,0)
                    val learningActivityModel = LearningActivityModel(fragment, questionItem)
                    activityInfo.add(learningActivityModel)


            } else if (fragment.equals("FragmentMcqTimeout")) {
                    val questions = jsonObject.getJSONObject("parameters")
                    var answerId = Int.MAX_VALUE
                    if (questions.has("answer_id")) {
                        answerId = questions.getInt("answer_id")
                        val questionString = questions.getString("question")
                        val questionItem =
                            QuestionItem(
                                questionString,
                                "",
                                "",
                                "",
                                "",
                                "",
                                answerId
                            )

                        val learningActivityModel = LearningActivityModel(fragment, questionItem)
                        activityInfo.add(learningActivityModel)
                    }
                    else if (!questions.has("answer_id")){
                        answerId = questions.getInt("answer_id")
                        val questionString = questions.getString("question")
                        val questionItem =
                            QuestionItem(
                                questionString,
                                "",
                                "",
                                "",
                                "",
                                "",
                                0
                            )

                        val learningActivityModel = LearningActivityModel(fragment, questionItem)
                        activityInfo.add(learningActivityModel)
                    }
                }

            } else if (fragment.equals("FragmentRightOrWrong")) {

                val statement = jsonObject.getJSONObject("parameters")
                val statementString = statement.getString("statement")
                val answer = statement.getString("answer")
                val statementItem = RightOrWrongItem(
                    statementString,
                    answer
                )

                val learningActivityModel = LearningActivityModel(fragment, statementItem)
                activityInfo.add(learningActivityModel)

            } else if (fragment.equals("FragmentTrueOrFalse")) {
                val questions = jsonObject.getJSONObject("parameters")
                val questionString = questions.getString("question")
                val answers = questions.getJSONArray("answers")
                val answerlist = ArrayList<Statement>()
                for (i in 0..answers.length() - 1) {
                    var obj = answers.getJSONObject(i)
                    var statement = obj.getString("statement")
                    var status = obj.getString("status")
                    answerlist.add(Statement(statement, status))
                }
                val leftbutton = questions.getString("button_left")
                val rightbutton = questions.getString("button_right")
                var tofdata = TrueOrFalseModel(questionString, answerlist, leftbutton, rightbutton)
                Log.d(TAG, "TOF: " + tofdata.list.size.toString())
                val learningActivityModel = LearningActivityModel(fragment, tofdata)
                activityInfo.add(learningActivityModel)
            } else if (fragment.equals("FragmentOperation") || fragment.equals("FragmentNotification")) {
                val session_type = jsonObject.getInt("notification_session_type")
                val parameter = jsonObject.getJSONArray("parameters")
                val notificationDataArray = ArrayList<OperationData>()
                for (i in 0..parameter.length() - 1) {
                    val obj = parameter.getJSONObject(i)
                    val text = obj.getString("text")
                    val color = obj.getString("color")
                    val shouldSendNotification = obj.getString("shouldSendNotification")
                    val shouldGoToNextActivity = obj.getString("shouldGoToNextActivity")
                    val pageImage = obj.getString("img")
                    val hasCountDown = obj.getString("hasCountDown")
                    val appContext = Config.instance.applicationContext!!
                    val drawableID = appContext.resources.getIdentifier(
                        pageImage,
                        "drawable",
                        appContext.packageName
                    )
                    val notificationData = OperationData(
                        text,
                        color,
                        shouldSendNotification.toBoolean(),
                        shouldGoToNextActivity.toBoolean(),
                        drawableID,
                        hasCountDown.toBoolean(),
                        session_type
                    )
                    notificationDataArray.add(notificationData)
                }
                val learningActivityModel = LearningActivityModel(fragment, notificationDataArray,skipText)
                activityInfo.add(learningActivityModel)
            } else if (fragment.equals("FragmentPermissions")) {
                val parameter = jsonObject.getJSONArray("parameters")
                val permissionsDataArray = ArrayList<OperationData>()
                for (i in 0..parameter.length() - 1) {
                    val obj = parameter.getJSONObject(i)
                    val text = obj.getString("text")
                    val color = obj.getString("color")
                    val shouldPauseForUserAction = obj.getString("shouldPauseForUserAction")
                    val shouldGoToNextActivity = obj.getString("shouldGoToNextActivity")
                    val pageImage = obj.getString("img")
                    val hasCountDown = obj.getString("hasCountDown")
                    val appContext = Config.instance.applicationContext!!
                    val drawableID = appContext.resources.getIdentifier(
                        pageImage,
                        "drawable",
                        appContext.packageName
                    )
                    val permissionsData = OperationData(
                        text,
                        color,
                        shouldPauseForUserAction.toBoolean(),
                        shouldGoToNextActivity.toBoolean(),
                        drawableID,
                        hasCountDown.toBoolean(),
                        0
                    )
                    permissionsDataArray.add(permissionsData)
                }
                val learningActivityModel = LearningActivityModel(fragment, permissionsDataArray,skipText)
                activityInfo.add(learningActivityModel)
            } else if (fragment.equals("ScreenLockTimeFragment")) {
                val parameter = jsonObject.getJSONArray("parameters")
                val screenLockTimeDataArray = ArrayList<OperationData>()
                for (i in 0 until parameter.length()) {
                    val obj = parameter.getJSONObject(i)
                    val text = obj.getString("text")
                    val color = obj.getString("color")
                    val shouldPauseForUserAction = obj.getString("shouldPauseForUserAction")
                    val shouldGoToNextActivity = obj.getString("shouldGoToNextActivity")
                    val pageImage = obj.getString("img")
                    val hasCountDown = obj.getString("hasCountDown")
                    val appContext = Config.instance.applicationContext!!
                    val drawableID = appContext.resources.getIdentifier(
                        pageImage,
                        "drawable",
                        appContext.packageName
                    )
                    val permissionsData = OperationData(
                        text,
                        color,
                        shouldPauseForUserAction.toBoolean(),
                        shouldGoToNextActivity.toBoolean(),
                        drawableID,
                        hasCountDown.toBoolean(),
                        0
                    )
                    screenLockTimeDataArray.add(permissionsData)
                }
                val learningActivityModel = LearningActivityModel(fragment, screenLockTimeDataArray, skipText)
                activityInfo.add(learningActivityModel)
            } else if (fragment.equals("FragmentStoryboard")) {
                val storyboardItems = ArrayList<StoryBoardItem>()
                val parameters = jsonObject.getJSONObject("parameters")
                val storyboardPages = parameters.getJSONArray("pages")
                for (i in 0 until storyboardPages.length()) {
                    val page = storyboardPages[i] as JSONObject
                    val title = page.getString("title")
                    val appContext = Config.instance.applicationContext!!
                    if (page.has("image")) {
                        val imageStr = page.getString("image")
                        val resId = appContext.resources.getIdentifier(
                            imageStr,
                            "drawable",
                            appContext.packageName
                        )
                        storyboardItems.add(StoryBoardItem(title, resId))
                    } else {
                        val animationStr = page.getString("animation")
                        val resId = appContext.resources.getIdentifier(
                            animationStr,
                            "raw",
                            appContext.packageName
                        )
                        storyboardItems.add(StoryBoardItem(title, 0, resId))
                    }
                }
                val learningActivityModel = LearningActivityModel(fragment, storyboardItems)
                activityInfo.add(learningActivityModel)
            } else if (fragment.equals("FragmentHtml")) {
                val parameters = jsonObject.getJSONObject("parameters")
                val displayText = parameters.getString("text")
                val sceneOption = parameters.getString("sceneOption").toInt()
                val shapeType = parameters.getString("shapeType").toInt()
                val outputData = HtmlData(displayText, sceneOption, shapeType)
                val learningActivityModel = LearningActivityModel(fragment, outputData)
                activityInfo.add(learningActivityModel)
            } else if (fragment.equals("FragmentKeypad")) {
                val parameters = jsonObject.getJSONObject("parameters")
                val displayText = parameters.getString("text")
                val sceneOption = parameters.getString("sceneOption").toInt()
                val sessionOption = parameters.getString("sessionOption").toInt()
                val outputData = HtmlData(displayText, sceneOption, sessionOption)
                val learningActivityModel = LearningActivityModel(fragment, outputData)
                activityInfo.add(learningActivityModel)
            } else if (fragment.equals("FragmentPinchAndZoom")) {
                val parameters = jsonObject.getJSONArray("parameters")
                val pinchAndZoomText = parameters.toArrayList()
                val learningActivityModel = LearningActivityModel(fragment, pinchAndZoomText)
                activityInfo.add(learningActivityModel)
            } else if (fragment.equals("FragmentQuickSettings")) {
                val parameter = jsonObject.getJSONArray("parameters")
                val qsDataArray = ArrayList<OperationData>()
                for (i in 0..parameter.length() - 1) {
                    val obj = parameter.getJSONObject(i)
                    val text = obj.getString("text")
                    val color = obj.getString("color")
                    val shouldPauseForUserAction = obj.getString("shouldPauseForUserAction")
                    val shouldGoToNextActivity = obj.getString("shouldGoToNextActivity")
                    val pageImage = obj.getString("img")
                    val hasCountDown = obj.getString("hasCountDown")

                    val appContext = Config.instance.applicationContext!!
                    val drawableID = appContext.resources.getIdentifier(
                        pageImage,
                        "drawable",
                        appContext.packageName
                    )
                    val qsData = OperationData(
                        text,
                        color,
                        shouldPauseForUserAction.toBoolean(),
                        shouldGoToNextActivity.toBoolean(),
                        drawableID,
                        hasCountDown.toBoolean()
                    )
                    qsDataArray.add(qsData)
                }
                val learningActivityModel = LearningActivityModel(fragment, qsDataArray,skipText)
                activityInfo.add(learningActivityModel)
            } else if (fragment.equals("FragmentComic")) {
                val parameters = jsonObject.getJSONObject("parameters")
                val titleName = parameters.getString("title")
                val sceneOption = parameters.getString("sceneOption").toInt()
                val sessionOption = parameters.getString("sessionOption").toInt()
                val comicdetails: MutableList<ComicDetails> = mutableListOf()
                val comicDetailsJson = parameters.getJSONArray("comic")
                for (i in 0 until comicDetailsJson.length()) {
                    val comicPage = ComicDetails("", mutableListOf())
                    val pageJson = comicDetailsJson.getJSONObject(i)
                    comicPage.background = pageJson.getString("background")
                    val speechBubblesJson = pageJson.getJSONArray("speechBubbles")
                    for (j in 0 until speechBubblesJson.length()) {
                        val oneBubbleJson = speechBubblesJson.getJSONObject(j)
                        val bubbleDisplayText = oneBubbleJson.getString("displayText")
                        val bubbleFontSize = oneBubbleJson.getString("fontSize")
                        val bubbleTextPosition = oneBubbleJson.getString("textPosition")
                        val bubbleTextBoxSize = oneBubbleJson.getString("textBoxSize")
                        val bubbleTextAlignment = oneBubbleJson.getString("textAlignment")
                        val bubbleImage = oneBubbleJson.getString("bubbleImage")
                        val bubblePosition = oneBubbleJson.getString("bubblePosition")
                        val bubbleSize = oneBubbleJson.getString("bubbleSize")
                        val oneSpeechBubble = ComicSpeechBubble(
                            bubbleDisplayText,
                            bubbleFontSize,
                            bubbleTextPosition,
                            bubbleTextBoxSize,
                            bubbleTextAlignment,
                            bubbleImage,
                            bubblePosition,
                            bubbleSize,
                            i.toString()
                        )
                        comicPage.comicSpeechBubbles.add(oneSpeechBubble)
                    }
                    comicdetails.add(comicPage)
                }
                val outputData = ComicData(titleName, sceneOption, sessionOption, comicdetails)
                val learningActivityModel = LearningActivityModel(fragment, outputData)
                activityInfo.add(learningActivityModel)

            } else if (fragment.equals("FragmentStory")) {
                val parameters = jsonObject.getJSONObject("parameters")
                val titleName = parameters.getString("title")
                val sceneOption = parameters.getString("sceneOption").toInt()
                val sessionOption = parameters.getString("sessionOption").toInt()
                val comicdetails: MutableList<ComicDetails> = mutableListOf()
                val outputData = ComicData(titleName, sceneOption, sessionOption, comicdetails)
                val learningActivityModel = LearningActivityModel(fragment, outputData)
                activityInfo.add(learningActivityModel)

            } else if (fragment.equals("NotificationSettingsFragment")) {
                val parameter = jsonObject.getJSONArray("parameters")
                val qsDataArray = ArrayList<OperationData>()
                for (i in 0..parameter.length() - 1) {
                    val obj = parameter.getJSONObject(i)
                    val text = obj.getString("text")
                    val color = obj.getString("color")
                    val shouldPauseForUserAction = obj.getString("shouldPauseForUserAction")
                    val shouldGoToNextActivity = obj.getString("shouldGoToNextActivity")
                    val pageImage = obj.getString("img")
                    val hasCountDown = obj.getString("hasCountDown")
                    var errorText = ""
                    if (obj.has("TextError")) {
                        errorText = obj.getString("TextError")
                    }
                    val appContext = Config.instance.applicationContext!!
                    val drawableID = appContext.resources.getIdentifier(
                        pageImage,
                        "drawable",
                        appContext.packageName
                    )
                    val qsData = OperationData(
                        text,
                        color,
                        shouldPauseForUserAction.toBoolean(),
                        shouldGoToNextActivity.toBoolean(),
                        drawableID,
                        hasCountDown.toBoolean(),
                        0,
                        errorText
                    )
                    qsDataArray.add(qsData)
                }
                val learningActivityModel = LearningActivityModel(fragment, qsDataArray,skipText)
                activityInfo.add(learningActivityModel)

            } else if (fragment.equals("FragmentContact")) {
                val parameter = jsonObject.getJSONArray("parameters")
                val qsDataArray = ArrayList<OperationData>()
                for (i in 0..parameter.length() - 1) {
                    val obj = parameter.getJSONObject(i)
                    val text = obj.getString("text")
                    val color = obj.getString("color")
                    val shouldPauseForUserAction = obj.getString("shouldPauseForUserAction")
                    val shouldGoToNextActivity = obj.getString("shouldGoToNextActivity")
                    val pageImage = obj.getString("img")
                    val hasCountDown = obj.getString("hasCountDown")
                    var errorText = ""
                    if (obj.has("TextError")) {
                        errorText = obj.getString("TextError")
                    }
                    val appContext = Config.instance.applicationContext!!
                    val drawableID = appContext.resources.getIdentifier(
                        pageImage,
                        "drawable",
                        appContext.packageName
                    )
                    val qsData = OperationData(
                        text,
                        color,
                        shouldPauseForUserAction.toBoolean(),
                        shouldGoToNextActivity.toBoolean(),
                        drawableID,
                        hasCountDown.toBoolean(),
                        0,
                        errorText
                    )
                    qsDataArray.add(qsData)
                }
                val learningActivityModel = LearningActivityModel(fragment, qsDataArray, skipText)
                activityInfo.add(learningActivityModel)
            }
            else if (fragment.equals("FragmentImportContact")) {
                val parameter = jsonObject.getJSONArray("parameters")
                val importContactArray = ArrayList<OperationData>()
                for (i in 0..parameter.length() - 1) {
                    val obj = parameter.getJSONObject(i)
                    val text = obj.getString("text")
                    val color = obj.getString("color")
                    val shouldPauseForUserAction = obj.getString("shouldPauseForUserAction")
                    val shouldGoToNextActivity = obj.getString("shouldGoToNextActivity")
                    val pageImage = obj.getString("img")
                    val hasCountDown = obj.getString("hasCountDown")
                    var errorText = ""
                    if (obj.has("TextError")) {
                        errorText = obj.getString("TextError")
                    }
                    val appContext = Config.instance.applicationContext!!
                    val drawableID = appContext.resources.getIdentifier(
                        pageImage,
                        "drawable",
                        appContext.packageName
                    )
                    val importData = OperationData(
                        text,
                        color,
                        shouldPauseForUserAction.toBoolean(),
                        shouldGoToNextActivity.toBoolean(),
                        drawableID,
                        hasCountDown.toBoolean(),
                        0,
                        errorText
                    )
                    importContactArray.add(importData)
                }
                val learningActivityModel = LearningActivityModel(fragment, importContactArray, skipText)
                activityInfo.add(learningActivityModel)
            } else if (fragment.equals("FragmentShareSheet")) {
                val parameter = jsonObject.getJSONArray("parameters")
                val qsDataArray = ArrayList<OperationData>()
                for (i in 0 until parameter.length()) {
                    val obj = parameter.getJSONObject(i)
                    val text = obj.getString("text")
                    val color = obj.getString("color")
                    val shouldPauseForUserAction = obj.getString("shouldPauseForUserAction")
                    val shouldGoToNextActivity = obj.getString("shouldGoToNextActivity")
                    val pageImage = obj.getString("img")
                    val hasCountDown = obj.getString("hasCountDown")
                    var errorText = ""
                    if (obj.has("TextError")){
                        errorText = obj.getString("TextError")
                    }
                    val appContext = Config.instance.applicationContext!!

                    val drawableID = appContext.resources.getIdentifier(pageImage, "drawable", appContext.packageName)
                    val qsData = OperationData(text, color, shouldPauseForUserAction.toBoolean(), shouldGoToNextActivity.toBoolean(), drawableID,hasCountDown.toBoolean(),0,errorText)
                    qsDataArray.add(qsData)
                }
                val learningActivityModel = LearningActivityModel(fragment, qsDataArray)
                activityInfo.add(learningActivityModel)
            }
            else if (fragment.equals("ScreenShotFragment")) {
                val parameter = jsonObject.getJSONArray("parameters")
                val ssDataArray = ArrayList<OperationData>()
                for (i in 0..parameter.length() - 1) {
                    val obj = parameter.getJSONObject(i)
                    val text = obj.getString("text")
                    val color = obj.getString("color")
                    val shouldPauseForUserAction = obj.getString("shouldPauseForUserAction")
                    val shouldGoToNextActivity = obj.getString("shouldGoToNextActivity")
                    val pageImage = obj.getString("img")
                    val hasCountDown = obj.getString("hasCountDown")
                    var errorText = ""
                    if (obj.has("TextError")) {
                        errorText = obj.getString("TextError")
                    }
                    val appContext = Config.instance.applicationContext!!
                    val drawableID = appContext.resources.getIdentifier(pageImage, "drawable", appContext.packageName)
                    val ssData = OperationData(text, color, shouldPauseForUserAction.toBoolean(), shouldGoToNextActivity.toBoolean(), drawableID,hasCountDown.toBoolean(),0,errorText)
                    ssDataArray.add(ssData)
                }
                val learningActivityModel  = LearningActivityModel(fragment, ssDataArray,skipText)
                activityInfo.add(learningActivityModel)
            }
            else if(fragment.equals("CallFragment")){
                val parameter = jsonObject.getJSONArray("parameters")
                val callDataArray = ArrayList<OperationData>()
                for (i in 0 until parameter.length()){
                    val obj = parameter.getJSONObject(i)
                    val text = obj.getString("text")
                    val color = obj.getString("color")
                    val shouldPauseForUserAction = obj.getString("shouldPauseForUserAction")
                    val shouldGoToNextActivity = obj.getString("shouldGoToNextActivity")
                    val pageImage = obj.getString("img")
                    val hasCountDown = obj.getString("hasCountDown")
                    var errorText = ""
                    if (obj.has("TextError")) {
                        errorText = obj.getString("TextError")
                    }
                    val appContext = Config.instance.applicationContext!!
                    val drawableID = appContext.resources.getIdentifier(
                        pageImage,
                        "drawable",
                        appContext.packageName
                    )
                    val callData = OperationData(
                        text,
                        color,
                        shouldPauseForUserAction.toBoolean(),
                        shouldGoToNextActivity.toBoolean(),
                        drawableID,
                        hasCountDown.toBoolean(),
                        0,
                        errorText
                    )
                    callDataArray.add(callData)
                }
                val learningActivityModel =
                    LearningActivityModel(fragment, callDataArray, skipText)
                activityInfo.add(learningActivityModel)
            }

            else if (fragment.equals("FragmentWidget") || fragment.equals("FragmentShortcut")||fragment.equals("FragmentNearby")) {
                val parameter = jsonObject.getJSONArray("parameters")
                val callDataArray = ArrayList<OperationData>()
                for (i in 0 until parameter.length()){
                    val obj = parameter.getJSONObject(i)
                    val text = obj.getString("text")
                    val color = obj.getString("color")
                    val shouldPauseForUserAction = obj.getString("shouldPauseForUserAction")
                    val shouldGoToNextActivity = obj.getString("shouldGoToNextActivity")
                    val pageImage = obj.getString("img")
                    val hasCountDown = obj.getString("hasCountDown")
                    var errorText = ""
                    if (obj.has("TextError")) {
                        errorText = obj.getString("TextError")
                    }
                    val appContext = Config.instance.applicationContext!!
                    val drawableID = appContext.resources.getIdentifier(
                        pageImage,
                        "drawable",
                        appContext.packageName
                    )
                    val callData = OperationData(
                        text,
                        color,
                        shouldPauseForUserAction.toBoolean(),
                        shouldGoToNextActivity.toBoolean(),
                        drawableID,
                        hasCountDown.toBoolean(),
                        0,
                        errorText
                    )
                    callDataArray.add(callData)
                }
                val learningActivityModel =
                    LearningActivityModel(fragment, callDataArray, skipText)
                activityInfo.add(learningActivityModel)

            }

            else if (fragment.equals("FragmentWidget")) {
                val parameter = jsonObject.getJSONArray("parameters")
                val widgetDataArray = ArrayList<OperationData>()

                for (i in 0 until parameter.length()) {
                    val obj = parameter.getJSONObject(i)
                    val text = obj.getString("text")
                    val color = obj.getString("color")
                    val shouldPauseForUserAction = obj.getString("shouldPauseForUserAction")
                    val shouldGoToNextActivity = obj.getString("shouldGoToNextActivity")
                    val pageImage = obj.getString("img")
                    val hasCountDown = obj.getString("hasCountDown")
                    val appContext = Config.instance.applicationContext!!
                    val drawableID = appContext.resources.getIdentifier(
                        pageImage,
                        "drawable",
                        appContext.packageName
                    )
                    val widgetData = OperationData(
                        text,
                        color,
                        shouldPauseForUserAction.toBoolean(),
                        shouldGoToNextActivity.toBoolean(),
                        drawableID,
                        hasCountDown.toBoolean()
                    )
                    widgetDataArray.add(widgetData)
                }
                val learningActivityModel = LearningActivityModel(fragment, widgetDataArray,skipText)
                activityInfo.add(learningActivityModel)
            } else if (fragment.equals("FragmentOrientation")) {
                val parameter = jsonObject.getJSONArray("parameters")
                val cameraDataArray = ArrayList<OperationData>()
                for (i in 0..parameter.length() - 1) {
                    val obj = parameter.getJSONObject(i)
                    val text = obj.getString("text")
                    val color = obj.getString("color")
                    val shouldPauseForUserAction = obj.getString("shouldPauseForUserAction")
                    val shouldGoToNextActivity = obj.getString("shouldGoToNextActivity")
                    val pageImage = obj.getString("img")
                    val appContext = Config.instance.applicationContext!!
                    val drawableID = appContext.resources.getIdentifier(pageImage, "drawable", appContext.packageName)
                    val orientationData = OperationData(text, color, shouldPauseForUserAction.toBoolean(), shouldGoToNextActivity.toBoolean(), drawableID,false)
                    cameraDataArray.add(orientationData)
                }
                val learningActivityModel = LearningActivityModel(fragment, cameraDataArray)
                activityInfo.add(learningActivityModel)
            }
            else if (fragment.equals("FragmentCamera")) {
                val parameter = jsonObject.getJSONArray("parameters")
                val orientationDataArray = ArrayList<OperationData>()
                for (i in 0..parameter.length() - 1) {
                    val obj = parameter.getJSONObject(i)
                    val text = obj.getString("text")
                    val color = obj.getString("color")
                    val shouldPauseForUserAction = obj.getString("shouldPauseForUserAction")
                    val shouldGoToNextActivity = obj.getString("shouldGoToNextActivity")
                    val pageImage = obj.getString("img")
                    val appContext = Config.instance.applicationContext!!
                    val drawableID = appContext.resources.getIdentifier(
                        pageImage,
                        "drawable",
                        appContext.packageName
                    )
                    val orientationData = OperationData(
                        text,
                        color,
                        shouldPauseForUserAction.toBoolean(),
                        shouldGoToNextActivity.toBoolean(),
                        drawableID,
                        false
                    )
                    orientationDataArray.add(orientationData)
                }
                val learningActivityModel = LearningActivityModel(fragment, orientationDataArray,skipText)
                activityInfo.add(learningActivityModel)
            } else if (fragment.equals("FragmentExplanation")) {
                val parameter = jsonObject.getJSONObject("parameters")
                val explanationText = parameter.getString("text")
                var animationId = 0
                if (parameter.has("animation")) {
                    val animationStr = parameter.getString("animation")
                    val appContext = Config.instance.applicationContext!!
                    animationId = appContext.resources.getIdentifier(
                        animationStr,
                        "raw",
                        appContext.packageName
                    )
                }
                val explanationData = InstructionData(explanationText, animationId)
                val learningActivityModel = LearningActivityModel(fragment, explanationData)
                activityInfo.add(learningActivityModel)
            } else if (fragment.equals("FragmentFeedback")) {
                val parameter = jsonObject.getJSONObject("parameters")
                val feedbackText = parameter.getString("text")
                var resId = 0
                var isAnimation = false
                val appContext = Config.instance.applicationContext!!
                if (parameter.has("img")) {
                    val img = parameter.getString("img")
                    resId =
                        appContext.resources.getIdentifier(img, "drawable", appContext.packageName)
                } else if (parameter.has("animation")) {
                    val img = parameter.getString("animation")
                    resId = appContext.resources.getIdentifier(img, "raw", appContext.packageName)
                    isAnimation = true
                }

                val feedbackData = InstructionData(feedbackText, resId, isAnimation)
                val learningActivityModel = LearningActivityModel(fragment, feedbackData)
                activityInfo.add(learningActivityModel)
            }


            if (jsonObject.has("hints")) {
                val hints = jsonObject.getJSONObject("hints")
                for (key in hints.keys()) {
                    hintsDictionary[key] = hints.getString(key)
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "json exception: " + e)
        }
    }

    private fun loadSessionFromAsset(context: Context): String {
        val charset: Charset = Charsets.UTF_8
        var json = ""
        try {
            val `is` = context.assets.open("session.json")
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            json = String(buffer, charset)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        Log.d("Json:", json)
        return json
    }

    fun JSONArray.toArrayList(): ArrayList<String> {
        val list = arrayListOf<String>()
        for (i in 0 until this.length()) {
            list.add(this.getString(i))
        }

        return list
    }


    override fun ChangeProgress() {
        GlobalScope.launch(progressDispatcher) {
            sessionCompletionProgress.value = getProgressValue()
        }
    }

    override fun storeUserName(inputName: String){
        GlobalScope.launch (progressDispatcher){
            storageManager?.storeUserInputName(inputName)
        }
    }

    override fun getUserName(): Flow<String> {
        return flow {
            emit(storageManager?.getUserInputName() ?: "")
        }
    }

    override fun saveProgress(completionStatus: Boolean) {
        GlobalScope.launch(progressDispatcher) {
            Log.i(TAG, "saveProgress() updating progress")
            activityCount = 0
            progressActivityCount = 0
            sessionCompletionProgress.value = 0
            val previousSessionsCompleted = storageManager?.getSessionsCompleted()
            firebaseAnalytics = Firebase.analytics
            Firebase.analytics.logEvent("simm_session_completed", null)
            logSessionDelay()

            endSessionText = if(completionStatus){
                Config.instance.applicationContext?.getString(R.string.well_done_you_finished_successfully) ?: "Well done! You completed the lesson!"
            } else{
                Config.instance.applicationContext?.getString(R.string.well_done) ?: "Well done!"
            }
            if(!completionStatus){
                sessionSkipped = true
            }

            if (sessionNumber == highestSessionIndex) {
                highestSessionIndex = sessionNumber + 1

                //send metric for sessioncount
                Log.i(
                    TAG,
                    "saveProgress() storing progress with next session number " + highestSessionIndex
                )
                if (highestSessionIndex < sessionModels.size) {
                    sessionModels[sessionNumber].hasPlayIcon = false
                    sessionModels[sessionNumber].hasCheckMark = true
                    sessionModels[highestSessionIndex].hasLockIcon = false
                    sessionModels[highestSessionIndex].hasPlayIcon = true
                    simmCommentFlow.value = sessionModels[highestSessionIndex].comment
                }

                if (sessionModels[sessionNumber].hasWidgetReveal) {
                    storageManager?.storeWidgetReveal(true)
                }
            } else {
                Log.i(
                    TAG,
                    "saveProgress() sessionNumber $sessionNumber less than highest session index $highestSessionIndex"
                )
                plantStageAnimationFlow.value = Pair(0,0)
            }

            val thisSessionStatus = when(completionStatus){
                true -> StorageManager.SessionStatus.Done
                false -> StorageManager.SessionStatus.AttemptedIncomplete
            }
            if(sessionModels[sessionNumber].completionStatus != StorageManager.SessionStatus.Done){
                sessionModels[sessionNumber].completionStatus = thisSessionStatus  // updates the home screen
            }
            storageManager?.storeSessionStatus(sessionNumber, thisSessionStatus)
            storageManager?.storeSessionTime(DateTimeFunctions.getCurrentDateTimeString())
            storageManager?.storeSessionTimeEpoch(DateTimeFunctions.getCurrentDateTimeEpochString())

            if(storageManager?.getSessionsCompleted() != previousSessionsCompleted){
                val plantStage = updatePlantStageAndCollection()

                sparklePlantStageFlow.value = 0
                if (plantStage == 4) {
                    sparklePlantCountFlow.value = 0
                    val plantcount = storageManager?.getPlantCount() ?: 0
                    //send metric for plant count

                    firebaseAnalytics.logEvent("simm_plant_fully_grown", null)
                }
                val plantCountSparkle = if (plantStage == 4) 0 else null
                sparkleFlow.value =
                    Pair(sparklePlantStageFlow.value, simmCommentFlow.value)
                setPlantStageAnimation(plantStage)
            }
            else{
                setPlantStageAnimation(0)
            }
        }
    }

    private suspend fun logSessionDelay() {
        val lastSessionTimeStamp: Long? = storageManager?.getLastSessionTimestamp()
        val currentSessionTimeStamp = System.currentTimeMillis()
        val delay =
            if (lastSessionTimeStamp != null) currentSessionTimeStamp - lastSessionTimeStamp else 0
        val dayMilliseconds: Long = 24 * 60 * 60 * 1000

        if (delay < dayMilliseconds) {
            //same day
            Firebase.analytics.logEvent("simm_session_completed_same_day", null)
        } else if (delay < 2 * dayMilliseconds) {
            //one day
            Firebase.analytics.logEvent("simm_session_completed_one_day_delay", null)
        } else {
            //two or more days
            Firebase.analytics.logEvent("simm_session_completed_two_days_plus", null)
        }

        if (delay > dayMilliseconds/2 && delay < dayMilliseconds) {
            //next day
            Firebase.analytics.logEvent("simm_session_completed_next_day", null)
        }

        storageManager?.storeLastSessionTimestamp(currentSessionTimeStamp)
    }

    override fun cancelSession() {
        GlobalScope.launch(progressDispatcher) {
            activityCount = 0
            progressActivityCount = 0
            sessionCompletionProgress.value = 0
        }
    }

    override fun updateState(state: String) {
        GlobalScope.launch(progressDispatcher) {
            Log.i(TAG, "received state $state")

            val currentLearningActivity = sessionActivityData[sessionNumber][activityCount]
            if (!(currentLearningActivity.fragment.equals("FragmentHtml") || currentLearningActivity.fragment.equals("FragmentQuickSettings")
                        ||currentLearningActivity.fragment.equals("FragmentPermissions"))) {
                Log.e(TAG, "${currentLearningActivity.fragment} unexpected state")
                return@launch
            }

            if (hintsDictionary.containsKey(state)) {
                hintFlow.value = hintsDictionary[state]
            } else {
                Log.i(TAG, "no hint")
            }
        }
    }

    val hintFlow: MutableStateFlow<String?> = MutableStateFlow(null)
    override fun getHintFlow(): Flow<String?> {
        return hintFlow
    }

    private suspend fun updatePlantStageAndCollection(): Int {
        // Update plant stage and collection flows
        val plantStage = storageManager?.getPlantGrowth() ?: 0
        plantStageFlow.value = Pair(storageManager?.getPlantGrowthAdjusted()?:0, storageManager?.getPlantGrowthMaxAdjusted()?:4)
        Log.i(TAG, "updatePlantStageAndCollection(): plant stage $plantStage")
        val plantCount = storageManager?.getPlantCount() ?: 0
        plantCountFlow.value = plantCount
        Log.i(TAG, "updatePlantStageAndCollection(): plant count $plantCount")
        val plantCollectionItems = storageManager?.getPlantCollection() ?: ArrayList()
        plantCollectionFlow.value = plantCollectionItems
        Log.i(TAG, "updatePlantStageAndCollection(): plant collection $plantCollectionItems")

        if (storageManager?.getWidgetReveal() != null && storageManager?.getWidgetReveal()!!) {
            updateSleepWakeState(
                storageManager?.getPlantType() ?: 0,
                storageManager?.getPlantState() ?: 0,
                plantStage
            )
        }
        return plantStage
    }

    private var prevPlantStage = 0
    private suspend fun setPlantStageAnimation(plantStage: Int) {
        val appContext = Config.instance.applicationContext!!
        Log.d("plantAnimationsstrgtst", "plantStage: $plantStage")
        val storedPlantType = storageManager?.getPlantType() ?: 0

        if(prevPlantStage >= 4) prevPlantStage = 0
        val plantType = min(storedPlantType, Plants.prePlantAnimations.size - 1)
        if (plantStage > 0) {
            plantStageAnimationFlow.value = Pair(
                appContext.resources.getIdentifier(
                    Plants.prePlantAnimations[plantType][(prevPlantStage).coerceIn(0,
                        Plants.prePlantAnimations[plantType].size - 1)], "raw", appContext.packageName),
                appContext.resources.getIdentifier(
                Plants.postPlantAnimations[plantType][(plantStage-1).coerceIn(0,
                    Plants.postPlantAnimations[plantType].size - 1)], "raw", appContext.packageName))
            Log.i("plantAnimations", "plantAnimations setting plant stage animation with pair $prevPlantStage, $plantStage")

            val preplant = Plants.prePlantAnimations[plantType][(prevPlantStage).coerceIn(0, Plants.prePlantAnimations[plantType].size - 1)]
            val postplant = Plants.postPlantAnimations[plantType][(plantStage-1).coerceIn(0, Plants.postPlantAnimations[plantType].size - 1)]
            Log.e("plantAnimations", "preplant: $preplant, postplant: $postplant")

            prevPlantStage = plantStage
            if(prevPlantStage >= 4) prevPlantStage = 0
        } else {
            plantStageAnimationFlow.value = Pair(0,0)
        }
    }

    override fun setSessionIndex(index: Int) {
        Log.i(TAG, "setSessionIndex() sessionNumber " + sessionNumber + ", index " + index)
        sessionNumber = index
        sessionModelFlow.value = sessionModels[sessionNumber]
    }

    override fun setHighestSessionIndex() {
        sessionNumber = highestSessionIndex
        sessionModelFlow.value = sessionModels[highestSessionIndex]
    }

    override fun getSessionNumberFlow(): Flow<Int> {
        return flow {
            emit(sessionNumber)
        }.flowOn(progressDispatcher)
    }

    override fun getPlantImageFlow(): Flow<Int> {
        return flow {
            storageManager?.plantImageFlow?.collect {
                emit(it ?: Plants.plantImages[1][0][0])
            }
        }.flowOn(progressDispatcher)
    }

    private val plantCountFlow: MutableStateFlow<Int> = MutableStateFlow(0)
    override fun getPlantCountFlow(): Flow<Int> {
        return plantCountFlow
    }

    override fun getPlantCountWithTotalFlow(): Flow<Pair<Int, Int>> {
        return flow {
            storageManager?.plantCountFlow?.collect {
                emit(Pair(it ?: 0, 30))
            }
        }.flowOn(progressDispatcher)
    }

    override fun getPlantCountSparkleFlow(): Flow<Int?> {
        return sparklePlantCountFlow
    }

    override fun getPlantStageSparkleFlow(): Flow<Int?> {
        return sparklePlantStageFlow
    }

    override fun getSparkleFlow(): Flow<Pair<Int?, String?>?> {
        return sparkleFlow
    }

    override fun getPlantStageAnimationFlow(): Flow<Pair<Int?,Int?>?> {
        return plantStageAnimationFlow
    }

    override fun getEndCelebrationAnimationFlow(): Flow<Int> {
        return flow {
            emit(sessionModels[sessionNumber].endAnim)
        }.flowOn(progressDispatcher)
    }

    private var sessionSkipped = false
    override fun getLessonCompleteAnimationFlow(): Flow<Int> {
        return flow {
            if(sessionSkipped){
                emit(R.raw.good_work)
                sessionSkipped = false
            }
            else{
                emit(R.raw.lesson_complete)
            }
        }.flowOn(progressDispatcher)
    }

    override fun finishSparklePlantCount() {
        GlobalScope.launch(progressDispatcher) {
            sparklePlantCountFlow.value = null
        }
    }

    override fun finishSparklePlantStage() {
        GlobalScope.launch(progressDispatcher) {
            sparklePlantStageFlow.value = null
        }
    }

    override fun finishSparkle() {
        GlobalScope.launch(progressDispatcher) {
            sparkleFlow.value = null
        }
    }

    override fun finishPlantStageAnimation() {
        GlobalScope.launch(progressDispatcher) {
            plantStageAnimationFlow.value = null
        }
    }

    private val plantStageFlow: MutableStateFlow<Pair<Int, Int>> = MutableStateFlow(Pair(0, 5))
    override fun getPlantStageFlow(): Flow<Pair<Int, Int>> {
        return plantStageFlow
    }

    override fun getPlantNameFlow(): Flow<String> {
        return flow {
            val plantType = storageManager?.getPlantType() ?: 0
            when (plantType) {
                0 -> emit("Purple Tulip")
                1 -> emit("Red Tulip")
                2 -> emit("Yellow Tulip")
                else -> emit("Plant")
            }
        }.flowOn(progressDispatcher)
    }

    val sessionModelFlow = MutableStateFlow<SessionModel?>(null)
    override fun getSessionModelFlow(): Flow<SessionModel?> {
        return sessionModelFlow
    }

    val simmCommentFlow = MutableStateFlow<String>("Learn and Explore!")
    override fun getSimmCommentFlow(): Flow<String> {
        return simmCommentFlow
    }

    override fun getSessionListFlow(): Flow<ArrayList<SessionModel>> {
        return flow {
            emit(sessionModels)
        }.flowOn(progressDispatcher)
    }

    val heartsStateFlow = MutableStateFlow<Int>(5)

    override fun getHeartsCountFlow(): Flow<Int> {
        return heartsStateFlow
    }

    override fun decrementHeartsCount() {
        GlobalScope.launch(progressDispatcher) {
            if (heartsStateFlow.value > 0) {
                heartsStateFlow.value = heartsStateFlow.value - 1
                storageManager?.storeHearts(heartsStateFlow.value)
            }
        }
    }

    private val hintsTextFlow = MutableStateFlow(ArrayList<String>())
    override fun getHintsTextFlow(): Flow<ArrayList<String>> {
        return hintsTextFlow
    }

    private val plantCollectionFlow = MutableStateFlow(ArrayList<PlantItem>())
    override fun getPlantCollectionFlow(): Flow<ArrayList<PlantItem>> {
        return plantCollectionFlow
    }

    override fun getPlantStageComment(): Flow<String> {
        return flow {
            emit("Complete a few more lessons to grow a full plant!")
        }
    }

    private var endSessionText: String = "Well done! You completed the lesson!"
    override fun getEndSessionText(): Flow<String> {
        return flow {
            emit(endSessionText)
        }
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

    private val sessionCompletionProgress = MutableStateFlow(0)
    override fun getSessionCompletionProgress(): Flow<Int> {
        return sessionCompletionProgress
    }

    private fun getProgressValue(): Int {
        var count = 0
        for (i in 0 until sessionActivityData[sessionNumber].size) {
            if (sessionActivityData[sessionNumber][i].fragment == "FragmentFeedback")
                count++
        }
        return ((progressActivityCount + 1) * 100) / (sessionActivityData[sessionNumber].size - count)
    }

    override fun getFragmentNameFlow(): Flow<String> {
        return flow {
            val currentLearningActivity = sessionActivityData[sessionNumber][activityCount]
            emit(currentLearningActivity.fragment)
        }.flowOn(progressDispatcher)
    }

    override fun getSkipTextFlow(): Flow<String> {
        return flow {
            val currentLearningActivity = sessionActivityData[sessionNumber][activityCount]
            emit(currentLearningActivity.skipText)
            Log.e("emit", currentLearningActivity.skipText)
        }.flowOn(progressDispatcher)
    }

    override fun hasNextActivityInSession(increment: Boolean): Boolean {
        operationStateFlow.value = null
        hintFlow.value = null
        activityCount++
        if (increment)
            progressActivityCount++
        Log.d(
            TAG,
            "hasNextActivityInSession: activityCount- " + progressActivityCount + ", sessionNumber- " + sessionNumber + " currlist-" + sessionActivityData[sessionNumber].size.toString()
        )
        val currentActivityList = sessionActivityData[sessionNumber]
        return activityCount < currentActivityList.size
    }

    override fun getMCQData(): Flow<QuestionItem> {
        return flow {
            val currentLearningActivity = sessionActivityData[sessionNumber][activityCount]
            try {
                val questionItem = currentLearningActivity.activityParams as QuestionItem
                emit(questionItem)
            } catch (e: Exception) {
                Log.e(TAG, "unexpected MCQ data: " + currentLearningActivity)
                throw e
            }
        }.flowOn(progressDispatcher)
    }

    var orientationStateFlow = MutableStateFlow<OperationData?>(null)
    var orientationPage = 0

    override fun getOrientationState(): Flow<OperationData?> {
        return orientationStateFlow
    }

    override fun nextOrientationPage() {
        orientationPage++

        val currentLearningActivity = sessionActivityData[sessionNumber][activityCount]
        val orientationDataArray =
            currentLearningActivity.activityParams as ArrayList<OperationData>
        val orientationData = orientationDataArray[orientationPage]
        // TODO: Ensure that drawable, boolean args are set correctly.
        orientationStateFlow.value = orientationData
    }

    override fun startOrientationData() {
        GlobalScope.launch(progressDispatcher) {
            val currentLearningActivity = sessionActivityData[sessionNumber][activityCount]
            val orientationDataArray =
                currentLearningActivity.activityParams as ArrayList<OperationData>
            orientationPage = 0
            orientationStateFlow.value = orientationDataArray[0]
        }
    }

    val operationStateFlow = MutableStateFlow<OperationData?>(null)
    var operationPage = 0

    override fun getOperationState(): Flow<OperationData?> {
        return operationStateFlow
    }

    override fun nextOperationPage() {
        operationPage++

        val currentLearningActivity = sessionActivityData[sessionNumber][activityCount]
        val operationDataArray = currentLearningActivity.activityParams as ArrayList<OperationData>
        val operationData = operationDataArray[operationPage]
        // TODO: Ensure that drawable, boolean args are set correctly.
        operationStateFlow.value = operationData
        Log.e("operationText", operationDataArray[operationPage].text)
    }

    override fun startOperationData() {

        GlobalScope.launch(progressDispatcher) {
            val currentLearningActivity = sessionActivityData[sessionNumber][activityCount]
            val operationDataArray =
                currentLearningActivity.activityParams as ArrayList<OperationData>
            operationPage = 0
            operationStateFlow.value = operationDataArray[0]
            Log.e("operationText", operationDataArray[0].text)
        }
    }

    override fun getStoryboardItems(): Flow<ArrayList<StoryBoardItem>> {
        return flow {
            val currentLearningActivity = sessionActivityData[sessionNumber][activityCount]
            try {
                val storyboardItems =
                    currentLearningActivity.activityParams as ArrayList<StoryBoardItem>
                emit(storyboardItems)
            } catch (e: Exception) {
                Log.e(TAG, "unexpected storyboard data: " + currentLearningActivity)
                throw e
            }
        }.flowOn(progressDispatcher)
    }

    override fun getSortingData(): Flow<TrueOrFalseModel> {
        return flow {
            val currentLearningActivity = sessionActivityData[sessionNumber][activityCount]
            try {
                val tOFData = currentLearningActivity.activityParams as TrueOrFalseModel
                val copyOfTOFData: TrueOrFalseModel = TrueOrFalseModel(tOFData.question, ArrayList(tOFData.list), tOFData.button_left, tOFData.button_right)
                emit(copyOfTOFData)
            } catch (e: Exception) {
                Log.e(TAG, "unexpected sorting data: " + currentLearningActivity)
                throw e
            }
        }.flowOn(progressDispatcher)
    }

    override fun getHtmlData(): Flow<HtmlData> {
        return flow {
            val currentLearningActivity = sessionActivityData[sessionNumber][activityCount]
            try {
                val outputHtmlData = currentLearningActivity.activityParams as HtmlData
                emit(outputHtmlData)
            } catch (e: Exception) {
                Log.e(TAG, "unexpected Html Data data: " + currentLearningActivity)
                throw e
            }
        }.flowOn(progressDispatcher)
    }

    override fun getComicData(): Flow<ComicData> {
        return flow {
            val currentLearningActivity = sessionActivityData[sessionNumber][activityCount]
            try {
                val outputComicData = currentLearningActivity.activityParams as ComicData
                emit(outputComicData)
            } catch (e: Exception) {
                Log.e(TAG, "unexpected Comic Data data: " + currentLearningActivity)
                throw e
            }
        }.flowOn(progressDispatcher)
    }


    override fun getPinchAndZoomText(): Flow<ArrayList<String>> {
        return flow {
            val currentLearningActivity = sessionActivityData[sessionNumber][activityCount]
            try {
                val pinchAndZoomText = currentLearningActivity.activityParams as ArrayList<String>
                emit(pinchAndZoomText)
            } catch (e: Exception) {
                Log.e(TAG, "unexpected pinch and zoom data: $currentLearningActivity")
                throw e
            }
        }.flowOn(progressDispatcher)
    }

    override fun getRightOrWrongData(): Flow<RightOrWrongItem> {
        return flow {
            val currentLearningActivity = sessionActivityData[sessionNumber][activityCount]
            try {
                val statementData = currentLearningActivity.activityParams as RightOrWrongItem
                emit(statementData)
            } catch (e: Exception) {
                Log.e(TAG, "unexpected statement data: $currentLearningActivity")
                throw e
            }
        }.flowOn(progressDispatcher)
    }

    override fun getExplanationText(): Flow<String> {
        return flow {
            val currentLearningActivity = sessionActivityData[sessionNumber][activityCount]
            try {
                val explanationText = currentLearningActivity.activityParams as String
                emit(explanationText)
            } catch (e: Exception) {
                Log.e(TAG, "unexpected explanation screen data: $currentLearningActivity")
                throw e
            }
        }.flowOn(progressDispatcher)
    }

    override fun getFeedbackData(): Flow<InstructionData> {
        return flow {
            val currentLearningActivity = sessionActivityData[sessionNumber][activityCount]
            try {
                val feedbackData = currentLearningActivity.activityParams as InstructionData
                emit(feedbackData)
            } catch (e: Exception) {
                Log.e(TAG, "unexpected feedback screen data: $currentLearningActivity")
                throw e
            }
        }.flowOn(progressDispatcher)
    }

    override fun checkAndUpdateAppIfNeeded(){
        GlobalScope.launch(progressDispatcher) {
            val isUpdated =  storageManager?.checkAppVersion() ?: false
            Log.i("ProgressManager", "Was version updated: $isUpdated")
        }
    }

    private fun updateSleepWakeState(plantType: Int, plantState: Int, plantGrowth: Int) {
        // PlantWidget
        val context = Config.instance.applicationContext
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val remoteViews = RemoteViews(context?.packageName, R.layout.plant_widget)
        val thisWidget = ComponentName(context!!, PlantWidget::class.java)
        remoteViews.setImageViewResource(
            R.id.iv_plant,
            Plants.plantImages[plantType][plantState][plantGrowth]
        )
        appWidgetManager.updateAppWidget(thisWidget, remoteViews)
    }

    companion object {
        val instance = ProgressManagerImpl()
    }
}