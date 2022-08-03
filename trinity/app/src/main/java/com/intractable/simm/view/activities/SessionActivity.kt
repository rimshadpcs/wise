package com.intractable.simm.view.activities

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.intractable.simm.R
import com.intractable.simm.databinding.ActivitySessionBinding
import com.intractable.simm.model.QuestionItem
import com.intractable.simm.utils.AppUtil
import com.intractable.simm.utils.Constants
import com.intractable.simm.view.fragments.*
import com.intractable.simm.viewmodel.SessionViewModel
import java.util.ArrayList


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

        sessionViewModel.getListActivity().observe(this) {
            if (it.isNotEmpty()) {
                sessionViewModel.eventlivedata.value = Constants.EVENT_NEXT_PAGE
//                sessionViewModel.eventlivedata.value = Constants.EVENT_NONE
            }
        }
        disableRightGesture()

        sessionViewModel.getFragment().observe(this) {
            when (it) {
                "FragmentMcq" -> {
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<FragmentMcq>(R.id.fragment_container_view)
                    }
                }
                "FragmentOperation" -> {
                    Log.e("activity", "operation")
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<FragmentOperation>(R.id.fragment_container_view)
                    }
                }
                "FragmentPermissions" -> {
                    Log.e("activity", "permissions")
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
                    Log.e("storyboard", "starting")
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<FragmentStoryboard>(R.id.fragment_container_view)
                    }
                }
                "FragmentNotification" -> {
                    Log.e("storyboard", "starting")
                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace<FragmentNotification>(R.id.fragment_container_view)
                    }
                }
            }
            sessionViewModel.getProgress()
        }

        this.sessionViewModel.getProgressValue().observe(this) {
            sessionBinding.sessionProgress.progress = it
        }

        sessionViewModel.getEvent().observe(this) {
            when (it) {
                Constants.EVENT_NEXT_PAGE -> {
                    sessionViewModel.eventlivedata.value = Constants.EVENT_NONE
                    val list = sessionViewModel.ListActivityData.value
                    sessionViewModel.quizFile.value =
                        list?.get(com.intractable.simm.ProgressManager.instance.sessionNumber)?.activityList?.get(
                            sessionViewModel.page)
                    Log.e("quiz", sessionViewModel.quizFile.value!!)
                    sessionViewModel.loadAllQuestions(this)
                }
                Constants.EVENT_FINISH_SESSION -> {
                    startActivity(
                        Intent(this, EndSessionActivity::class.java)
                    )
                    finish()
                }
                Constants.EVENT_SHOW_RIGHT_BOTTOMSHEET -> {
                    val data = sessionViewModel.mcqData.value
                    val theQuestion: String = data!![sessionViewModel.current_question].question
                    val actualRightAnswer: String = data[sessionViewModel.current_question].answer
                    val modalBottomSheet = RightBottomSheetDialog(actualRightAnswer, theQuestion)
                    modalBottomSheet.show(supportFragmentManager, RightBottomSheetDialog.TAG)
                }
                Constants.EVENT_SHOW_WRONG_BOTTOMSHEET -> {
                    val data: ArrayList<QuestionItem>? = sessionViewModel.mcqData.value
                    val theQuestion: String = data!![sessionViewModel.current_question].question
                    val actualRightAnswer: String = data[sessionViewModel.current_question].answer
                    val modalBottomSheet = WrongBottomSheetDialog(actualRightAnswer, theQuestion)
                    modalBottomSheet.show(supportFragmentManager, WrongBottomSheetDialog.TAG)
                }
            }
        }

    }

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
