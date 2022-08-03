package com.intractable.simm.view.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.intractable.simm.databinding.ActivityBubbleSampleBinding

import com.intractable.simm.utils.AppUtil
import com.intractable.simm.utils.Constants
import com.intractable.simm.viewmodel.BubbleViewModel
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class BubbleSample : AppCompatActivity() {

    lateinit var appUtil: AppUtil

    private val bubbleViewModel: BubbleViewModel by viewModels()
    private lateinit var bubbleBinding: ActivityBubbleSampleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bubbleBinding = ActivityBubbleSampleBinding.inflate(layoutInflater)
        setContentView(bubbleBinding.root)
        appUtil = AppUtil(this)
        appUtil.setDarkMode()

       // bubbleBinding.tvMessage.text = bubbleSampleMessage
      //  bubbleBinding.btContinue.text = bubbleSampleButton
        bubbleBinding.btContinue.setOnClickListener{
            val intent = Intent(this, SessionActivity::class.java)
            intent.putExtra("FROM", Constants.FLAG_BUBBLE_SAMBLE)
            startActivity(intent)
            finish()
        }

    }
}
