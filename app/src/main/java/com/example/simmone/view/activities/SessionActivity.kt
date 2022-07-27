package com.example.simmone.view.activities

import android.R.attr.left
import android.R.attr.right
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowInsets
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.ViewCompat.setSystemGestureExclusionRects
import androidx.core.view.doOnLayout
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.simmone.ProgressManager
import com.example.simmone.R
import com.example.simmone.databinding.ActivitySessionBinding
import com.example.simmone.utils.AppUtil
import com.example.simmone.utils.Constants
import com.example.simmone.view.fragments.*
import com.example.simmone.viewmodel.SessionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SessionActivity : AppCompatActivity(),RightBottomSheetDialog.RightBottomSheetListener,
WrongBottomSheetDialog.WrongBottomSheetListener{

    private lateinit var sessionBinding: ActivitySessionBinding
    private val sessionViewModel:SessionViewModel by viewModels()
    lateinit var appUtil: AppUtil
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionBinding = ActivitySessionBinding.inflate(layoutInflater)
        setContentView(sessionBinding.root)
        appUtil = AppUtil(this)
        appUtil.setDarkMode()

        sessionBinding.btClose.setOnClickListener {
            appUtil.showLoginDialog()
        }

        sessionViewModel.loadSession(this)

        sessionViewModel.getListActivity().observe(this, Observer {
            if (!it.isEmpty()){
                sessionViewModel.eventlivedata.value = Constants.EVENT_NEXT_PAGE
//                sessionViewModel.eventlivedata.value = Constants.EVENT_NONE
            }
        })
        DisableRightGesture()

        sessionViewModel.getFragment().observe(this, Observer {
            when(it){
                "FragmentMcq" -> {
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<FragmentMcq>(R.id.fragment_container_view)
                    }
                }
                "FragmentOperation" -> {
                    Log.e("activity","operation")
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<FragmentOperation>(R.id.fragment_container_view)
                    }
                }
                "FragmentPermissions" -> {
                    Log.e("activity","permissions")
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<FragmentPermissions>(R.id.fragment_container_view)
                    }
                }
                "FragmentTrueOrFalse" -> {
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<FragmentTrueOrFalse>(R.id.fragment_container_view)
                    }
                }
                "FragmentStoryboard" -> {
                    Log.e("storyboard","starting")
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<FragmentStoryboard>(R.id.fragment_container_view)
                    }
                }
                "FragmentNotification" -> {
                    Log.e("storyboard","starting")
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<FragmentNotification>(R.id.fragment_container_view)
                    }
                }
            }
            sessionViewModel.getProgress()
        })

        sessionViewModel.getProgressValue().observe(this, Observer {
            sessionBinding.sessionProgress.progress = it
        })

        sessionViewModel.getEvent().observe(this, Observer {
            when(it){
                Constants.EVENT_NEXT_PAGE -> {
                    sessionViewModel.eventlivedata.value = Constants.EVENT_NONE
                    var list = sessionViewModel.ListActivityData.value
                    sessionViewModel.quizFile.value =
                        list?.get(ProgressManager.instance.sessionNumber)?.activityList?.get(sessionViewModel.page)
                    Log.e("quiz", sessionViewModel.quizFile.value!!)
                    sessionViewModel.loadAllQuestions(this)
                }
                Constants.EVENT_FINISH_SESSION -> {
                    startActivity(
                        Intent(this,EndSessionActivity::class.java)
                    )
                    finish()
                }
                Constants.EVENT_SHOW_RIGHT_BOTTOMSHEET -> {
                    val data = sessionViewModel.mcqData.value
                    val theQuestion: String = data!![sessionViewModel.current_question].question
                    val actualRightAnswer: String = data!![sessionViewModel.current_question].answer
                    val modalBottomSheet = RightBottomSheetDialog(actualRightAnswer,theQuestion)
                    modalBottomSheet.show(supportFragmentManager, RightBottomSheetDialog.TAG)
                }
                Constants.EVENT_SHOW_WRONG_BOTTOMSHEET -> {
                    val data = sessionViewModel.mcqData.value
                    val theQuestion: String = data!![sessionViewModel.current_question].question
                    val actualRightAnswer: String = data!![sessionViewModel.current_question].answer
                    val modalBottomSheet = WrongBottomSheetDialog(actualRightAnswer,theQuestion)
                    modalBottomSheet.show(supportFragmentManager, WrongBottomSheetDialog.TAG)
                }
            }
        })

    }

    private fun DisableRightGesture() {
        sessionBinding.rootview.doOnLayout {
            val gestureInsets = sessionBinding.rootview.rootWindowInsets.getInsets(WindowInsets.Type.systemGestures())

            val rectHeight = sessionBinding.rootview.height
            val rectTop = 0
            val rectBottom = sessionBinding.rootview.bottom


            // Left Rect values
            val leftExclusionRectLeft = 0
            val leftExclusionRectRight = gestureInsets.left

            val leftExclusionRect = Rect(
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
        sessionViewModel.checkForNextQuestion()
    }

    override fun onWrongButtonClicked(text: String?) {
        sessionViewModel.checkForNextQuestion()
    }

    override fun onBackPressed() {
        appUtil.showLoginDialog()
    }

    override fun onResume() {
        super.onResume()
        if (Constants.PAGE_FLAG>-1) {
            Constants.PAGE_FLAG = -1
            Log.e("next",sessionViewModel.page.toString())
            sessionViewModel.checkForNextQuestion()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        Log.e("New","Intent")
            sessionViewModel.notPage = sessionViewModel.notPage+1
            sessionViewModel.notTxtLivedata.value = sessionViewModel.notItems[sessionViewModel.notPage]

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.i("SessionActivity", "permission result")
        sessionViewModel.permissionPage++
        sessionViewModel.permissionTxtLivedata.value = sessionViewModel.permissionItems[sessionViewModel.permissionPage]
    }

}
