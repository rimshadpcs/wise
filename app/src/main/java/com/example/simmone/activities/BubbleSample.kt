package com.example.simmone.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.simmone.databinding.ActivityBubbleSampleBinding

class BubbleSample : AppCompatActivity() {
    private lateinit var bubbleBinding: ActivityBubbleSampleBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        bubbleBinding = ActivityBubbleSampleBinding.inflate(layoutInflater)
        setContentView(bubbleBinding.root)

        bubbleBinding.btContinue.setOnClickListener{
            val intent = Intent(this, McqActivity::class.java)
            intent.putExtra("FROM", 0)
            startActivity(intent)

        }

    }
}