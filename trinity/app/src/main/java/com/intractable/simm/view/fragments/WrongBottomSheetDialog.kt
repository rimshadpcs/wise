package com.intractable.simm.view.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.media.MediaPlayer
import android.os.*
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.intractable.simm.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.*
import javax.annotation.Nullable

class WrongBottomSheetDialog(actualRightAnswer: String, private var theQuestion: String) : BottomSheetDialogFragment() {
    private var theRightAnswer =actualRightAnswer

    companion object {
        const val TAG = "WrongModalBottomSheet"
    }
    private lateinit var mListenerRightSheet: WrongBottomSheetListener

    @SuppressLint("ClickableViewAccessibility")
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.wrong_bottom_sheet, container, false)

        val tvCorrectAnswer = view.findViewById<TextView>(R.id.tvCorrectAnswerWrong)
        val tvTheQuestion = view.findViewById<TextView>(R.id.tvTheQuestionWrong)
        val btContinueWrong = view.findViewById<Button>(R.id.btContinueMcqWrong)

        tvTheQuestion.text = theQuestion
        tvCorrectAnswer.paintFlags = tvCorrectAnswer.paintFlags or tvCorrectAnswer.paintFlags
        tvCorrectAnswer.text = theRightAnswer
        dialog?.window?.setDimAmount(0F)

        GlobalScope.launch (Dispatchers.Main){
            delay(2000)
            dialog?.window?.setDimAmount(.5F)

        }

        // haptics
        val vib = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context?.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context?.getSystemService(AppCompatActivity.VIBRATOR_SERVICE) as Vibrator
        }

        // this type of vibration requires API 29
        val vibrationEffect: VibrationEffect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val doubleClickPattern = longArrayOf(0, 10, 160, 10)
            val doubleClickAmplitude = intArrayOf(0, 255, 0, 255)

            vibrationEffect = VibrationEffect.createWaveform(doubleClickPattern, doubleClickAmplitude, -1)

            // it is safe to cancel other vibrations currently taking place
            vib.cancel();
            vib.vibrate(vibrationEffect);
        }

        var mp : MediaPlayer = MediaPlayer.create(context, R.raw.quiz_wrong_answer)
        if(mp.isPlaying) {
            mp.pause() // Pause the current track
        }
        mp.start()

        btContinueWrong.setOnClickListener {

            if (mp.isPlaying) {
                mp.pause() // Pause the current track
            }
            mp.release()
            mp = MediaPlayer.create(context, R.raw.button_press)
            mp.start()  // play sound

            mListenerRightSheet.onWrongButtonClicked("Clicked")
            dismiss()
        }



        btContinueWrong.setOnTouchListener { _, motionEvent: MotionEvent ->
            when (motionEvent.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    btContinueWrong.setBackgroundResource(R.drawable.no_shadow_white)
                    val params =
                        btContinueWrong.layoutParams as ViewGroup.MarginLayoutParams
                    params.bottomMargin = params.bottomMargin - 15
                    btContinueWrong.layoutParams = params
                }

                MotionEvent.ACTION_UP -> {
                    btContinueWrong.setBackgroundResource(R.drawable.white_shadow_button_bg)
                    val params =
                        btContinueWrong.layoutParams as ViewGroup.MarginLayoutParams
                    params.bottomMargin = params.bottomMargin + 15
                    btContinueWrong.layoutParams = params
                }
            }
            false
        }

        return view
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        val bottomSheet = view.parent as View
        bottomSheet.backgroundTintMode = PorterDuff.Mode.CLEAR
        bottomSheet.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
        bottomSheet.setBackgroundColor(Color.TRANSPARENT)
        bottomSheet.setBackgroundResource(R.drawable.bottom_sheet_corner_green)
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
                "$context must implement BottomSheetListener"
            )
        }
    }
}