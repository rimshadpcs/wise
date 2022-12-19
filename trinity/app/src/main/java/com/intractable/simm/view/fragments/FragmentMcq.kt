package com.intractable.simm.view.fragments

import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.intractable.simm.R
import com.intractable.simm.databinding.FragmentMcqBinding
import com.intractable.simm.model.QuestionItem
import com.intractable.simm.view.activities.SessionActivity
import com.intractable.simm.viewmodel.SessionViewModel


class FragmentMcq : Fragment(),RightBottomSheetDialog.RightBottomSheetListener,
    WrongBottomSheetDialog.WrongBottomSheetListener ,View.OnClickListener {

    private lateinit var mcqBinding: FragmentMcqBinding
    private val mcqModel: SessionViewModel by activityViewModels()
    private var currentQuestion = 0
    private var correct = 0
    private var incorrect = 1
    private lateinit var questionItems: QuestionItem


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.transition_right)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mcqBinding = FragmentMcqBinding.inflate(inflater, container, false)
        val view = mcqBinding.root
        Log.e("MCQ", mcqModel.page.toString())


        mcqModel.mcqData.observe(context as SessionActivity) {
            if (it != null) {
                questionItems = it
                mcqModel.questionItem = it
                setQuestionOnPage()
                Log.e("MCQ", questionItems.question)
                Firebase.analytics.logEvent("simm_mcq_started", null)
            }
        }
        return view
    }

    private fun setQuestionOnPage() {
        mcqBinding.tvQuestion.text = questionItems.question
        mcqBinding.btChoice1.text = questionItems.choice1
        mcqBinding.btChoice2.text = questionItems.choice2
        mcqBinding.btChoice3.text = questionItems.choice3
        mcqBinding.btChoice4.text = questionItems.choice4

        // BUTTON 1 Click
        mcqBinding.btChoice1.setOnClickListener {
            Log.e("MCQ", mcqModel.page.toString())
            validateChoice1()

            //checkForNextQuestion()
        }
        // BUTTON 2 Click
        mcqBinding.btChoice2.setOnClickListener {
            validateChoice2()

            //checkForNextQuestion()
        }
        // BUTTON 3 Click
        mcqBinding.btChoice3.setOnClickListener {
            validateChoice3()

            //checkForNextQuestion()
        }
        // BUTTON 4 Click
        mcqBinding.btChoice4.setOnClickListener {
            validateChoice4()

            //checkForNextQuestion()
        }
    }

    private fun validateChoice1(){
        mcqBinding.btChoice1.setBackgroundResource(R.drawable.choice1_selected)

        if(questionItems.answerId != 0){

            if (questionItems.answerId == 1) {
                callRightBottomSheet()
                correct++

            }
            else{
                incorrect++
                callWrongBottomSheet()
            }
        }

        else if (questionItems.choice1==questionItems.answer){
            correct++
            callRightBottomSheet()
        }
        else{
            correct++
            callWrongBottomSheet()

        }
    }

    private fun validateChoice2(){
        mcqBinding.btChoice2.setBackgroundResource(R.drawable.choice2_selected)
        if(questionItems.answerId != 0){

            if (questionItems.answerId == 2) {
                callRightBottomSheet()
                correct++

            }
            else{
                incorrect++
                callWrongBottomSheet()
            }
        }

        else if (questionItems.choice2 == questionItems.answer
        ) {
            correct++
            callRightBottomSheet()
        }
        else{
            incorrect++
            callWrongBottomSheet()
        }
    }
    private fun validateChoice3(){
        mcqBinding.btChoice3.setBackgroundResource(R.drawable.choice3_selected)

        if(questionItems.answerId != 0){

            if (questionItems.answerId == 3) {
                callRightBottomSheet()
                correct++

            }
            else{
                incorrect++
                callWrongBottomSheet()
            }
        }

        else if (questionItems.choice3 == questionItems.answer
        ) {
            correct++
            callRightBottomSheet()
        }
        else{
            incorrect++
            callWrongBottomSheet()
        }
    }

    private fun validateChoice4(){
        mcqBinding.btChoice4.setBackgroundResource(R.drawable.choice4_selected)


        if(questionItems.answerId != 0){

            if (questionItems.answerId == 4) {
                callRightBottomSheet()
                correct++

            }
            else{
                incorrect++
                callWrongBottomSheet()
            }
        }

        else if (questionItems.choice4 == questionItems.answer
        ) {
            correct++
            callRightBottomSheet()
        }
        else{
            incorrect++
            callWrongBottomSheet()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            FragmentMcq().apply {
                arguments = Bundle().apply {
                }
            }
    }

    private fun callRightBottomSheet(){
        logEndOfMcq()
        mcqModel.currentQuestion = currentQuestion
        mcqModel.eventlivedata.value = SessionViewModel.EVENT_SHOW_RIGHT_BOTTOMSHEET
        mcqModel.eventlivedata.value = SessionViewModel.EVENT_NONE
    }
    private fun callWrongBottomSheet(){
        logEndOfMcq()
        mcqModel.currentQuestion = currentQuestion
        mcqModel.eventlivedata.value = SessionViewModel.EVENT_SHOW_WRONG_BOTTOMSHEET
        mcqModel.eventlivedata.value = SessionViewModel.EVENT_NONE
    }

    override fun onRightButtonClicked(text: String?) {
        mcqModel.checkForNextQuestion(true)
    }

    override fun onWrongButtonClicked(text: String?) {
        mcqModel.checkForNextQuestion(true)
    }

    private fun logEndOfMcq() {
        Firebase.analytics.logEvent("simm_mcq_completed", null)
    }

    override fun onClick(v: View?) {

    }
}