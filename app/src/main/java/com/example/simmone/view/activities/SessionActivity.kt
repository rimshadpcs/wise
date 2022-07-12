package com.example.simmone.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.Observer
import com.example.simmone.R
import com.example.simmone.databinding.ActivitySessionBinding
import com.example.simmone.utils.Constants
import com.example.simmone.view.fragments.FragmentMcq
import com.example.simmone.view.fragments.FragmentOperation
import com.example.simmone.view.fragments.RightBottomSheetDialog
import com.example.simmone.view.fragments.WrongBottomSheetDialog
import com.example.simmone.viewmodel.SessionViewModel
import javax.security.auth.callback.Callback

class SessionActivity : AppCompatActivity(),RightBottomSheetDialog.RightBottomSheetListener,
WrongBottomSheetDialog.WrongBottomSheetListener{

    private lateinit var sessionBinding: ActivitySessionBinding
    private val sessionViewModel:SessionViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionBinding = ActivitySessionBinding.inflate(layoutInflater)
        setContentView(sessionBinding.root)

            sessionViewModel.loadSession(this)

        sessionViewModel.getListActivity().observe(this, Observer {
            if (!it.isEmpty()){
                sessionViewModel.eventlivedata.value = Constants.EVENT_NEXT_PAGE
//                sessionViewModel.eventlivedata.value = Constants.EVENT_NONE
            }
        })

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
            }
        })

        sessionViewModel.getEvent().observe(this, Observer {
            when(it){
                Constants.EVENT_NEXT_PAGE -> {
                    sessionViewModel.eventlivedata.value = Constants.EVENT_NONE
                    var list = sessionViewModel.ListActivityData.value
                    when(list?.get(sessionViewModel.session_num)?.sessionId){
                        "session_1" -> {
                            sessionViewModel.quizFile.value = list[sessionViewModel.session_num].activityList?.get(sessionViewModel.page)
                            Log.e("quiz",sessionViewModel.quizFile.value!!)
                            sessionViewModel.loadAllQuestions(this)
                        }
                    }
                }
                Constants.EVENT_FINISH_SESSION -> {
                    startActivity(
                        Intent(this,EndSessionActivity::class.java)
                    )
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

    override fun onRightButtonClicked(text: String?) {
        sessionViewModel.checkForNextQuestion()
    }

    override fun onWrongButtonClicked(text: String?) {
        sessionViewModel.checkForNextQuestion()
    }

    override fun onResume() {
        super.onResume()
        if (Constants.PAGE_FLAG>-1) {
            Log.e("next",sessionViewModel.page.toString())
            sessionViewModel.checkForNextQuestion()
        }
    }
}