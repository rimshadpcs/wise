package com.example.simmone.view.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.simmone.databinding.ActivityMcqBinding
import com.example.simmone.model.QuestionItem
import com.example.simmone.view.fragments.RightBottomSheetDialog
import com.example.simmone.view.fragments.WrongBottomSheetDialog
import com.example.simmone.viewmodel.McqViewModel

class McqActivity : AppCompatActivity(),
    RightBottomSheetDialog.RightBottomSheetListener,
    WrongBottomSheetDialog.WrongBottomSheetListener {

    private lateinit var mcqBinding: ActivityMcqBinding
    private val mcqModel: McqViewModel by viewModels()
    private var currentQuestion = 0
    private var correct = 0
    private var incorrect = 1
    private lateinit var questionItems: List<QuestionItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mcqBinding = ActivityMcqBinding.inflate(layoutInflater)
        setContentView(mcqBinding.root)
        if (intent.getIntExtra("FROM", 0) == 0) {
            mcqModel.quizFile.value = "mcq1.json"
        } else if (intent.getIntExtra("FROM", 0) == 1) {
            mcqModel.quizFile.value = "mcq2.json"
        }
        mcqModel.mcqData.observe(this) {
            questionItems = it
            setQuestionOnPage()
        }
    }

    override fun onStart() {
        mcqModel.loadAllQuestions(this)
        super.onStart()
    }

    private fun setQuestionOnPage() {
        mcqBinding.tvQuestion.text = questionItems[currentQuestion].question
        mcqBinding.btChoice1.text = questionItems[currentQuestion].choice1
        mcqBinding.btChoice2.text = questionItems[currentQuestion].choice2
        mcqBinding.btChoice3.text = questionItems[currentQuestion].choice3
        mcqBinding.btChoice4.text = questionItems[currentQuestion].choice4

        // BUTTON 1 Click
        mcqBinding.btChoice1.setOnClickListener {
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

    private fun checkForNextQuestion(){
        // load next question if any
        if (currentQuestion < questionItems.size - 1) {
            currentQuestion++
            setQuestionOnPage()
        } else {
            if (intent.getIntExtra("FROM", 0) == 0) {
                val intent = Intent(this, OperationActivity::class.java)
                startActivity(intent)
                finish()
            } else {

                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("NextSession", 2)
                startActivity(intent)
                finish()
            }
        }
    }
    private fun callRightBottomSheet(){
        val theQuestion: String =questionItems[currentQuestion].question
        val actualRightAnswer: String =questionItems[currentQuestion].answer
        val modalBottomSheet = RightBottomSheetDialog(actualRightAnswer,theQuestion)
        modalBottomSheet.show(supportFragmentManager, RightBottomSheetDialog.TAG)
    }
    private fun callWrongBottomSheet(){
        val theQuestion: String =questionItems[currentQuestion].question
        val actualRightAnswer: String =questionItems[currentQuestion].answer
        val modalBottomSheet = WrongBottomSheetDialog(actualRightAnswer,theQuestion)
        modalBottomSheet.show(supportFragmentManager, WrongBottomSheetDialog.TAG)
    }

    override fun onRightButtonClicked(text: String?) {
        checkForNextQuestion()
    }
    override fun onWrongButtonClicked(text: String?) {
        checkForNextQuestion()
    }

}