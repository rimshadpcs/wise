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
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.intractable.simm.R
import kotlinx.coroutines.*
import javax.annotation.Nullable


class RightBottomSheetDialog(actualRightAnswer: String, private var theQuestion: String) : BottomSheetDialogFragment() {
    private var theRightAnswer = actualRightAnswer

    companion object {
        const val TAG = "RightModalBottomSheet"
    }
    private lateinit var mListenerRightSheet: RightBottomSheetListener

    @SuppressLint("ClickableViewAccessibility")
    @OptIn(DelicateCoroutinesApi::class)
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
            val singleClickPattern = longArrayOf(0, 10)
            val singleClickAmplitude = intArrayOf(0, 180)

            vibrationEffect = VibrationEffect.createWaveform(singleClickPattern, singleClickAmplitude, -1)

            // it is safe to cancel other vibrations currently taking place
            vib.cancel();
            vib.vibrate(vibrationEffect);
        }


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

        btContinueRight.setOnTouchListener { _, motionEvent: MotionEvent ->
            when (motionEvent.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    btContinueRight.setBackgroundResource(R.drawable.no_shadow_white)
                    val params =
                        btContinueRight.layoutParams as ViewGroup.MarginLayoutParams
                    params.bottomMargin = params.bottomMargin - 15
                    btContinueRight.layoutParams = params
                }

                MotionEvent.ACTION_UP -> {
                    btContinueRight.setBackgroundResource(R.drawable.white_shadow_button_bg)
                    val params =
                        btContinueRight.layoutParams as ViewGroup.MarginLayoutParams
                    params.bottomMargin = params.bottomMargin + 15
                    btContinueRight.layoutParams = params
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