package com.intractable.simm.view.fragments

import android.os.Bundle
import android.provider.Settings
import android.transition.TransitionInflater
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.intractable.simm.R
import com.intractable.simm.databinding.FragmentMcqBinding
import com.intractable.simm.model.QuestionItem
import com.intractable.simm.view.activities.SessionActivity
import com.intractable.simm.viewmodel.SessionViewModel


// Almost the same as FragmentMcq but with special options
class FragmentMcqTimeout : Fragment(),RightBottomSheetDialog.RightBottomSheetListener,
    WrongBottomSheetDialog.WrongBottomSheetListener ,View.OnClickListener {

    private lateinit var mcqBinding: FragmentMcqBinding
    private val mcqModel: SessionViewModel by activityViewModels()
    private var currentQuestion = 0
    private var correct = 0
    private var incorrect = 1
    private lateinit var questionItems: QuestionItem

    private var screenTimeoutTime = 0
    private var screenLockTime = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.transition_right)

        screenTimeoutTime = Settings.System.getInt(requireContext().contentResolver, Settings.System.SCREEN_OFF_TIMEOUT)
        screenLockTime = Settings.Secure.getInt(requireContext().contentResolver, "lock_screen_lock_after_timeout", 5000)
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
                generateQuestionItems()
                setQuestionOnPage()
                Log.e("MCQ", questionItems.question)
                Firebase.analytics.logEvent("simm_mcq_started") {
                    param(FirebaseAnalytics.Param.ITEM_NAME, it.question.substring(0, 15))
                }
            }
        }
        return view
    }

    private fun generateQuestionItems(){
        val answer = msToString(screenTimeoutTime + screenLockTime)
        val choices = arrayOf(answer, "", "", "")
        var possibleChoices = mutableListOf<Int>()

        if(screenTimeoutTime > 0){
            possibleChoices.add(screenLockTime)
        }
        if(screenLockTime > 0){
            possibleChoices.add(screenTimeoutTime)
        }
        if(screenTimeoutTime + screenLockTime - 60 * 1000 * 5 > 0){
            possibleChoices.add(screenTimeoutTime + screenLockTime - 60 * 1000 * 5) // - 5 minutes
        }
        if(screenTimeoutTime + screenLockTime - 60 * 1000 * 10 > 0){
            possibleChoices.add(screenTimeoutTime + screenLockTime - 60 * 1000 * 10) // - 10 minutes
        }
        possibleChoices = possibleChoices.distinct().toMutableList()

        var multiplier = 1
        while(possibleChoices.size < 3){
            possibleChoices.add(screenTimeoutTime + screenLockTime + 60 * 1000 * 5 * multiplier) // + 5 minutes
            multiplier++
        }

        possibleChoices.shuffle()
        choices[1] = msToString(possibleChoices[0])
        choices[2] = msToString(possibleChoices[1])
        choices[3] = msToString(possibleChoices[2])
        choices.shuffle()
        questionItems = QuestionItem(
            questionItems.question,
            choices[0],
            choices[1],
            choices[2],
            choices[3],
            answer,
            answerId = "".toInt()
        )
        mcqModel.questionItem = questionItems
    }
    fun msToString(inputTimeMS: Int): String{
        if(inputTimeMS == 0){
            return "0 seconds"
        }
        val oneMinute = 60 * 1000
        if(inputTimeMS < oneMinute){  // display in seconds
            if(inputTimeMS%1000 == 0){
                return (inputTimeMS/1000).toString() + " seconds"
            }
            else{
                return "around " + (inputTimeMS/1000).toString() + " seconds"
            }
        }
        else{  // display in minutes
            if(inputTimeMS%oneMinute == 0){
                return (inputTimeMS/oneMinute).toString() + " minutes"
            }
            else{
                return (inputTimeMS/oneMinute).toString() + " minutes and " + ((inputTimeMS%oneMinute)/1000).toString() + " seconds"
            }
        }

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
        if (questionItems.choice1==questionItems.answer){
            correct++
            callRightBottomSheet()
        }
        else{
            incorrect++
            callWrongBottomSheet()

        }
    }

    private fun validateChoice2(){
        mcqBinding.btChoice2.setBackgroundResource(R.drawable.choice2_selected)
        if (questionItems.choice2 == questionItems.answer
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
        if (questionItems.choice3 == questionItems.answer
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

        if (questionItems.choice4 == questionItems.answer
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
            FragmentMcqTimeout().apply {
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
        Firebase.analytics.logEvent("simm_mcq_completed") {
            param(FirebaseAnalytics.Param.ITEM_NAME, questionItems.question.substring(0, 15))
        }
    }

    override fun onClick(v: View?) {

    }
}