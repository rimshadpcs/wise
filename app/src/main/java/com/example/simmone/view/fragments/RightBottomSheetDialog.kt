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


class RightBottomSheetDialog(actualRightAnswer: String, private var theQuestion: String) : BottomSheetDialogFragment() {
    private var theRightAnswer = actualRightAnswer

    companion object {
        const val TAG = "RightModalBottomSheet"
    }
    private lateinit var mListenerRightSheet: RightBottomSheetListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.right_bottom_sheet, container, false)
        val btContinueRight = view.findViewById<Button>(R.id.btContinueMcqRight)

        val tvCorrectAnswer = view.findViewById<TextView>(R.id.tvCorrectAnswerRight)
        val tvTheQuestion = view.findViewById<TextView>(R.id.tvTheQuestionRight)

        tvTheQuestion.text = theQuestion
        tvCorrectAnswer.paintFlags = tvCorrectAnswer.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        tvCorrectAnswer.text = theRightAnswer

        btContinueRight.setOnClickListener {
            mListenerRightSheet.onRightButtonClicked("Clicked")
            dismiss()
        }

        return view
    }

    interface RightBottomSheetListener {
        fun onRightButtonClicked(text: String?)
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListenerRightSheet = context as RightBottomSheetListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                context.toString()
                    .toString() + " must implement BottomSheetListener"
            )
        }
    }
}