package com.intractable.simm.services

import android.os.Handler
import android.util.AttributeSet
import androidx.compose.Context
import com.intractable.simm.utils.TextUtil
import kotlinx.coroutines.*
import java.lang.Runnable

class Typewriter : androidx.appcompat.widget.AppCompatTextView {
    private var mText: CharSequence? = null
    private var mIndex = 0
    private var mDelay: Long = 500 //Default 500ms delay

    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {}

    @OptIn(DelicateCoroutinesApi::class)
    private val coScope : CoroutineScope = GlobalScope
    private val mHandler: Handler = Handler()

    fun animateText(text2: CharSequence?) {
        val descriptionText = TextUtil.updateTextWithIcons(text2!!, context, paint.fontMetrics)
        mText = descriptionText
        mIndex = 0
//        val mp : MediaPlayer = MediaPlayer.create(context, R.raw.dialogue_sound)

        val characterAdder: Runnable = object : Runnable {
            override fun run() {
                text = mText!!.subSequence(0, mIndex++)
                if (mIndex <= mText!!.length) {
//                    if(mIndex % (Random.Default.nextInt(3) + 1) == 0){
//                        // play sound effect
//                        if(mp.isPlaying) {
//                            mp.pause() // Pause the current track
//                        }
//                        mp.start()  // play sound
//                    }
                    mHandler.postDelayed(this, mDelay)
                }
            }
        }

//
//        coScope.launch(Dispatchers.Main) {
//            mText = text!!.subSequence(0,mIndex++)
//            if(mIndex<=mText!!.length){
//                delay(mDelay)
//            }
//        }
//
//        suspend {
//            characterAdder
//        }
        mHandler.removeCallbacks(characterAdder)


        mHandler.postDelayed(characterAdder, mDelay)
    }

    fun setCharacterDelay(millis: Long) {
        mDelay = millis
    }
}