package com.intractable.simm.view.fragments

import android.content.Context
import android.graphics.Paint
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.intractable.simm.R
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

        var mp : MediaPlayer = MediaPlayer.create(context, R.raw.quiz_correct_answer)
        if(mp.isPlaying) {
            mp.pause() // Pause the current track
        }
        mp.start()

        btContinueRight.setOnClickListener {
            if (mp.isPlaying) {
                mp.pause() // Pause the current track
            }
            mp.release()
            mp = MediaPlayer.create(context, R.raw.button_press)
            mp.start()  // play sound

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
                "$context must implement BottomSheetListener"
            )
        }
    }
}