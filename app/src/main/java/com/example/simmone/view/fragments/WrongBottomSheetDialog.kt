package com.example.simmone.view.fragments

import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.simmone.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
class WrongBottomSheetDialog(actualRightAnswer: String, private var theQuestion: String) : BottomSheetDialogFragment() {
    private var theRightAnswer =actualRightAnswer

    companion object {
        const val TAG = "WrongModalBottomSheet"
    }
    private lateinit var mListenerRightSheet: WrongBottomSheetListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.wrong_bottom_sheet, container, false)
        val btContinueWrong = view.findViewById<Button>(R.id.btContinueMcqWrong)

        val tvCorrectAnswer = view.findViewById<TextView>(R.id.tvCorrectAnswerWrong)
        val tvTheQuestion = view.findViewById<TextView>(R.id.tvTheQuestionWrong)

        tvTheQuestion.text = theQuestion
        tvCorrectAnswer.paintFlags = tvCorrectAnswer.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        tvCorrectAnswer.text = theRightAnswer
        btContinueWrong.setOnClickListener {
            mListenerRightSheet.onWrongButtonClicked("Clicked")
            dismiss()
        }

        return view
    }

    interface WrongBottomSheetListener {
        fun onWrongButtonClicked(text: String?)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListenerRightSheet = context as WrongBottomSheetListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                context.toString()
                    .toString() + " must implement BottomSheetListener"
            )
        }
    }
}