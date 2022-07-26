package com.example.simmone.view.fragments

import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.simmone.R
import com.example.simmone.databinding.FragmentMcqBinding
import com.example.simmone.model.QuestionItem
import com.example.simmone.utils.Constants
import com.example.simmone.view.activities.SessionActivity
import com.example.simmone.viewmodel.SessionViewModel


class FragmentMcq : Fragment(),RightBottomSheetDialog.RightBottomSheetListener,
    WrongBottomSheetDialog.WrongBottomSheetListener ,View.OnClickListener {

    private lateinit var mcqBinding: FragmentMcqBinding
    private val mcqModel: SessionViewModel by activityViewModels()
    private var currentQuestion = 0
    private var correct = 0
    private var incorrect = 1
    private lateinit var questionItems: List<QuestionItem>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.transition_right)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mcqBinding = FragmentMcqBinding.inflate(inflater, container, false)
        val view = mcqBinding.root
        Log.e("MCQ", mcqModel.page.toString())

        mcqModel.getMcQData().observe(context as SessionActivity, Observer {
            if (!it.isEmpty()) {
                questionItems = it
                setQuestionOnPage()
                Log.e("MCQ", questionItems[0].question)
            }
        })
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setQuestionOnPage() {
        mcqBinding.tvQuestion.text = questionItems[currentQuestion].question
        mcqBinding.btChoice1.text = questionItems[currentQuestion].choice1
        mcqBinding.btChoice2.text = questionItems[currentQuestion].choice2
        mcqBinding.btChoice3.text = questionItems[currentQuestion].choice3
        mcqBinding.btChoice4.text = questionItems[currentQuestion].choice4

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
        if (questionItems[currentQuestion].choice1==questionItems[currentQuestion].answer){
            correct++
            callRightBottomSheet()
        }
        else{
            incorrect++
            callWrongBottomSheet()
        }
    }
    private fun validateChoice2(){
        if (questionItems[currentQuestion].choice2 == questionItems[currentQuestion].answer
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
        if (questionItems[currentQuestion].choice3 == questionItems[currentQuestion].answer
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
        if (questionItems[currentQuestion].choice4 == questionItems[currentQuestion].answer
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
        fun newInstance(param1: String, param2: String) =
            FragmentMcq().apply {
                arguments = Bundle().apply {
                }
            }
    }

    private fun callRightBottomSheet(){
        mcqModel.current_question = currentQuestion
        mcqModel.eventlivedata.value = Constants.EVENT_SHOW_RIGHT_BOTTOMSHEET
        mcqModel.eventlivedata.value = Constants.EVENT_NONE
    }
    private fun callWrongBottomSheet(){
        mcqModel.current_question = currentQuestion
        mcqModel.eventlivedata.value = Constants.EVENT_SHOW_WRONG_BOTTOMSHEET
        mcqModel.eventlivedata.value = Constants.EVENT_NONE
    }

    override fun onRightButtonClicked(text: String?) {
        mcqModel.checkForNextQuestion()
    }

    override fun onWrongButtonClicked(text: String?) {
        mcqModel.checkForNextQuestion()
    }

    override fun onClick(v: View?) {

    }
}