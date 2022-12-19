package com.intractable.simm.view.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Point
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowInsets
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.intractable.simm.R
import com.intractable.simm.databinding.ActivitySessionBinding
import com.intractable.simm.gamelogic.Config
import com.intractable.simm.utils.AppUtil
import com.intractable.simm.utils.Constants
import com.intractable.simm.view.fragments.*
import com.intractable.simm.viewmodel.SessionViewModel
import kotlinx.coroutines.launch


class SessionActivity : AppCompatActivity(),RightBottomSheetDialog.RightBottomSheetListener,
WrongBottomSheetDialog.WrongBottomSheetListener{
    private val TAG = "SessionActivity"

    private lateinit var sessionBinding: ActivitySessionBinding
    private val sessionViewModel:SessionViewModel by viewModels()
    lateinit var appUtil: AppUtil
    lateinit var getResult:ActivityResultLauncher<Intent>


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionBinding = ActivitySessionBinding.inflate(layoutInflater)
        setContentView(sessionBinding.root)
        val cancelSession: () -> Unit = {
            sessionViewModel.cancelSession()
        }
        appUtil = AppUtil(this, cancelSession)
        appUtil.setDarkMode()

        setResultLauncher()


        sessionBinding.btClose.setOnClickListener {
            appUtil.showExitDialog()
        }

        sessionViewModel.heartsLiveData.observe(this ){
            sessionBinding.tvHeartCount.text = it.toString()
            if (it == 0)
                showHeartBottomSheet()
        }

        sessionViewModel.eventlivedata.value = SessionViewModel.EVENT_NEXT_PAGE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            disableRightGesture()
        }

        sessionViewModel.getFragment().observe(this) {
            sessionBinding.progressView.visibility = View.VISIBLE

            when (it) {
                "ScreenShotFragment" -> {
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<ScreenShotFragment>(R.id.fragment_container_view)
                        this.addToBackStack(null)
                    }
                }
                "FragmentImportContact" -> {
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<FragmentImportContact>(R.id.fragment_container_view)
                        this.addToBackStack(null)
                    }
                }
                "FragmentContact" -> {
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<FragmentContact>(R.id.fragment_container_view)
                        this.addToBackStack(null)
                    }
                }
                "FragmentShareSheet" -> {
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<FragmentShareSheet>(R.id.fragment_container_view)
                    }
                }
                "FragmentMcq" -> {
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<FragmentMcq>(R.id.fragment_container_view)
                    }
                }
                "FragmentMcqTimeout" -> {
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<FragmentMcqTimeout>(R.id.fragment_container_view)
                    }
                }
                "FragmentPermissions" -> {
                    Log.e("activity", "permissions")
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<PermissionFragment>(R.id.fragment_container_view)
                    }
                }
                "FragmentTrueOrFalse" -> {
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<FragmentTrueOrFalse>(R.id.fragment_container_view)
                    }
                }
                "FragmentStoryboard" -> {
                    Log.e("storyboard", "starting")
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<FragmentStoryboard>(R.id.fragment_container_view)
                    }
                }
                "FragmentNotification" -> {
                    Log.e("Session","Started")
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<NotificationFragment>(R.id.fragment_container_view)
                    }
                }
                "ScreenLockTimeFragment" -> {
                    Log.e("Session","Started")
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<ScreenLockTimeFragment>(R.id.fragment_container_view)
                    }
                }
                "FragmentHtml" -> {
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<FragmentHtml>(R.id.fragment_container_view)
                    }
                }
                "FragmentKeypad" -> {
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<FragmentKeypad>(R.id.fragment_container_view)
                    }
                }
                "FragmentWidget" -> {
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<WidgetFragment>(R.id.fragment_container_view)
                    }
                }
                "NotificationSettingsFragment" -> {
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<NotificationSettingsFragment>(R.id.fragment_container_view)
                    }
                }
                "FragmentQuickSettings" -> {
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<QSettingsFragment>(R.id.fragment_container_view)
                    }
                }
                "FragmentRightOrWrong" -> {
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<RightOrWrongFragment>(R.id.fragment_container_view)
                    }
                }
                "FragmentFeedback" -> {
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<FragmentFeedback>(R.id.fragment_container_view)
                    }
                }
                "FragmentExplanation" -> {
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<FragmentExplanation>(R.id.fragment_container_view)
                    }
                }
                "FragmentOrientation" -> {
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<FragmentOrientation>(R.id.fragment_container_view)
                    }
                }
                "FragmentCamera" -> {
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<FragmentCamera>(R.id.fragment_container_view)
                    }
                }
                "FragmentComic" -> {
                    sessionBinding.progressView.visibility = View.GONE
                    supportFragmentManager.commit{
                        setReorderingAllowed(true)
                        replace<FragmentComic>(R.id.fragment_container_view)
                    }
                }
                "FragmentStory" -> {
                    sessionBinding.progressView.visibility = View.GONE
                    supportFragmentManager.commit{
                        setReorderingAllowed(true)
                        replace<FragmentComic>(R.id.fragment_container_view)
                    }
                }
                "CallFragment" -> {
                    supportFragmentManager.commit{
                        setReorderingAllowed(true)
                        replace<CallFragment>(R.id.fragment_container_view)
                    }
                }
                "FragmentNearby" -> {
                    supportFragmentManager.commit{
                        setReorderingAllowed(true)
                        replace<FragmentNearBy>(R.id.fragment_container_view)
                    }
                }

            }
        }

        sessionViewModel.progress.observe(this) {
            sessionBinding.sessionProgress.progress = it
            if (it>0 && sessionViewModel.shouldSparkle && it < 100) {
                setLottieAnimation(it)
            }else{
                sessionViewModel.shouldSparkle = true
            }
        }

        sessionViewModel.plantStageAnimationFlow.observe(this) {
            if (it != null) {
                startActivity(
                    Intent(this, LessonCompleteActivity::class.java)
                )
                finish()
            }
        }

        sessionViewModel.getEvent().observe(this) { it ->
            when (it) {
                SessionViewModel.EVENT_NEXT_PAGE -> {
                    sessionViewModel.eventlivedata.value = SessionViewModel.EVENT_NONE
                    Config.instance.progressManager.getFragmentNameFlow().asLiveData().observe(this) {
                        Log.e("CurrentFragment" , it)
                        sessionViewModel.fragmentLiveData.value = it
                    }
                    Config.instance.progressManager.getSkipTextFlow().asLiveData().observe(this) {
                        sessionViewModel.skipTextFlow = it
                    }
                }
                SessionViewModel.EVENT_FINISH_SESSION -> {
                    Config.instance.progressManager.saveProgress()
                }

                SessionViewModel.EVENT_CHECK_CAMERA_PERMISSION -> {
                    Log.e("event","called")
                    if (cameraPermissionEnabled()){
                        sessionViewModel.eventlivedata.value = SessionViewModel.EVENT_START_CAMERA
                    }
                    else{
                        ActivityCompat.requestPermissions(
                            this,
                            REQUIRED_PERMISSIONS,
                            SessionViewModel.REQUEST_CODE_PERMISSIONS
                        )
                    }
                }

                SessionViewModel.EVENT_SHOW_RIGHT_BOTTOMSHEET -> {

                    sessionViewModel.changeProgress()
                        val data = sessionViewModel.questionItem
                        val theQuestion: String = data.question
                        val actualRightAnswer: String = data.answer
                        val modalBottomSheet =
                            RightBottomSheetDialog(actualRightAnswer, theQuestion)
                        modalBottomSheet.show(supportFragmentManager, RightBottomSheetDialog.TAG)
                        modalBottomSheet.isCancelable = false

                }
                SessionViewModel.EVENT_SHOW_WRONG_BOTTOMSHEET -> {

                    sessionViewModel.shouldSparkle = false
                    sessionViewModel.changeProgress()
                    sessionViewModel.decrementHeartsCount()
                        val data = sessionViewModel.questionItem
                        val theQuestion: String = data.question
                        val actualRightAnswer: String = data.answer
                        val modalBottomSheet =
                            WrongBottomSheetDialog(actualRightAnswer, theQuestion)
                        modalBottomSheet.show(supportFragmentManager, WrongBottomSheetDialog.TAG)
                        modalBottomSheet.isCancelable = false

                }
                SessionViewModel.EVENT_SHOW_RIGHT_BOTTOMSHEET_STATEMENT -> {
                    sessionViewModel.changeProgress()

                        val data = sessionViewModel.rightOrWrongItem
                        val theStatement: String = data.statement
                        val actualRightAnswer: String = data.answer
                        val modalBottomSheet =
                            RightBottomSheetDialog(actualRightAnswer, theStatement)
                        modalBottomSheet.show(supportFragmentManager, RightBottomSheetDialog.TAG)
                        modalBottomSheet.isCancelable = false


                }
                SessionViewModel.EVENT_SHOW_WRONG_BOTTOMSHEET_STATEMENT -> {
                    sessionViewModel.changeProgress()
                    sessionViewModel.decrementHeartsCount()

                        val data = sessionViewModel.rightOrWrongItem
                        val theStatement: String = data.statement
                        val actualRightAnswer: String = data.answer
                        val modalBottomSheet =
                            WrongBottomSheetDialog(actualRightAnswer, theStatement)
                        modalBottomSheet.show(supportFragmentManager, WrongBottomSheetDialog.TAG)
                        modalBottomSheet.isCancelable = false

                }
            }
        }
    }

    private fun cameraPermissionEnabled(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    this,android.Manifest.permission.CAMERA,

                    ) -> {
                    return true
                }
                else -> {
                    return false
                }
            }
        }
        return false
    }

    private fun setResultLauncher() {
        getResult =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()) {
                if (cameraPermissionEnabled()){
                    Log.e("ggjhhj","hgh")
                    if (sessionViewModel.fragmentLiveData.value.equals("FragmentCamera"))
                        sessionViewModel.eventlivedata.value = SessionViewModel.EVENT_START_CAMERA
                    else
                        sessionViewModel.nextOperationPage()
                }else{
                    Log.e("ggjhhj","h")
                    sessionViewModel.eventlivedata.value = SessionViewModel.EVENT_SHOW_SKIP_BUTTON
                }
            }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }


    private fun setLottieAnimation(progress:Int) {
        val maxSizePoint = Point()
        windowManager.defaultDisplay.getSize(maxSizePoint)
        val maxX = maxSizePoint.x
        val delta: Float = 15f * resources.displayMetrics.density
        val position: Float = progress.toFloat() * (sessionBinding.sessionProgress.getWidth() - delta) / sessionBinding.sessionProgress.getMax()
        val lottieX: Float = position - sessionBinding.ivSessionLottie.getWidth() / 2 + delta
        val finalX =
            if (sessionBinding.ivSessionLottie.getWidth() + lottieX > maxX) maxX - sessionBinding.ivSessionLottie.getWidth() else lottieX
        sessionBinding.ivSessionLottie.translationX = finalX.toFloat()
        sessionBinding.ivSessionLottie.playAnimation()
    }

    private fun showHeartBottomSheet() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.hearts_bottom_sheet,null)
        dialog.setContentView(view)
        dialog.show()
        val btOk = view.findViewById<TextView>(R.id.btOk)
        btOk.setOnClickListener {
            dialog.dismiss()
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun disableRightGesture() {
        sessionBinding.rootview.doOnLayout {
            val gestureInsets = sessionBinding.rootview.rootWindowInsets.getInsets(WindowInsets.Type.systemGestures())

            sessionBinding.rootview.height
            val rectTop = 0
            val rectBottom = sessionBinding.rootview.bottom


            // Left Rect values
            val leftExclusionRectLeft = 0
            val leftExclusionRectRight = gestureInsets.left

            Rect(
                leftExclusionRectLeft,
                rectTop,
                leftExclusionRectRight,
                rectBottom
            )

            // Right Rect values
            val rightExclusionRectLeft = sessionBinding.rootview.right - gestureInsets.right
            val rightExclusionRectRight = sessionBinding.rootview.right


            // Rect for gestures on the right side of the screen
            val rightExclusionRect = Rect(
                rightExclusionRectLeft,
                rectTop,
                rightExclusionRectRight,
                rectBottom
            )

            // Add both rects and exclude gestures
            sessionBinding.root.systemGestureExclusionRects = listOf(rightExclusionRect)

        }
    }



    override fun onRightButtonClicked(text: String?) {
        sessionViewModel.checkForNextQuestion(true)
    }

    override fun onWrongButtonClicked(text: String?) {
        sessionViewModel.checkForNextQuestion(true)
    }

    @Deprecated("Deprecated in Java", ReplaceWith("appUtil.showExitDialog()"))
    override fun onBackPressed() {
        appUtil.showExitDialog()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && Constants.QSClicked){
            sessionViewModel.nextOperationPage()
            Constants.QSClicked = false
        }
        if (sessionViewModel.fragmentLiveData.value.equals("FragmentNotification")||
            sessionViewModel.fragmentLiveData.value.equals("FragmentQuickSettings") && sessionViewModel.operationDataLiveData.value != null){
            if (sessionViewModel.operationDataLiveData.value!!.shouldDoAction
                && !hasFocus){
                Log.e("window","Changed")
                sessionViewModel.eventlivedata.value = SessionViewModel.EVENT_SHOW_NOTIFICATION_BAR
                sessionViewModel.eventlivedata.value = SessionViewModel.EVENT_NONE
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
//        sessionViewModel.finishNotificationsession()

        sessionViewModel.nextOperationPage()
        Log.e("hjh",sessionViewModel.operationDataLiveData.value!!.sessionType.toString())
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            sessionBinding.progressView.visibility = View.GONE
        }else{
            sessionBinding.progressView.visibility = View.VISIBLE
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SessionViewModel.REQUEST_CODE_PERMISSIONS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (sessionViewModel.fragmentLiveData.value.equals("FragmentCamera")){
                    sessionViewModel.eventlivedata.value = SessionViewModel.EVENT_START_CAMERA
                    sessionViewModel.eventlivedata.value = SessionViewModel.EVENT_NONE
                }else {
                    sessionViewModel.nextOperationPage()
                }
            }
            else{
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)){
                    sessionViewModel.showSkipButton()
                }else{
                    if (sessionViewModel.cameraPermissionDenied){
                        val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri: Uri = Uri.fromParts("package", packageName, null)
                        settingsIntent.data = uri
                        SessionViewModel.launchedPermissionScreen = true
                        getResult.launch(settingsIntent)
                    }else{
                        lifecycleScope.launch {
                            Config.instance.storageManager?.storePermissionStatus(true)
                            sessionViewModel.showSkipButton()
                        }
                    }
                }
            }
        }
        else if (requestCode == SessionViewModel.REQUEST_CODE_PERMISSIONS_NEARBY){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                sessionViewModel.eventlivedata.value = SessionViewModel.EVENT_CHECK_NEARBY_PERMISSION
                sessionViewModel.eventlivedata.value = SessionViewModel.EVENT_NONE
            }else {
                sessionViewModel.showSkipButton()
            }
        }
        else if (requestCode == SessionViewModel.REQUEST_CODE_READ_AND_WRITE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                sessionViewModel.nextOperationPage()
            }else {
                sessionViewModel.showSkipButton()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
            || (keyCode == KeyEvent.KEYCODE_VOLUME_UP)){
            sessionViewModel.eventlivedata.value = SessionViewModel.EVENT_CAPTURE_IMAGE
        }
        return true;
    }

    companion object{

        const val INTENT_APPSETTINGS = 365

        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

}
