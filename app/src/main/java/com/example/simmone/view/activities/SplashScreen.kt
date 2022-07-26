package com.example.simmone.view.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.ui.lerp
import com.example.simmone.R
import com.example.simmone.databinding.ActivitySplashScreenBinding
import kotlin.math.max
import kotlin.math.roundToInt


@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    private lateinit var splashBinding: ActivitySplashScreenBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        splashBinding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(splashBinding.root)

        val endMargin = 5
        val startMargin = 300



        val loadTime: Long = 3000
        val timePerFrame: Long = 50
        val tickIncrease = timePerFrame.toFloat() / loadTime.toFloat() * 1.1f // 1.1 because there seems to be some rounding errors when adding

        var barProgress = 0f
        val scale = resources.displayMetrics.density
        val fiveDPAsPixels = (5 * scale).roundToInt()
        splashBinding.clLoadingBarWrapper.setPadding(fiveDPAsPixels, fiveDPAsPixels, fiveDPAsPixels, fiveDPAsPixels)

        object : CountDownTimer(loadTime, timePerFrame) {

            override fun onTick(millisUntilFinished: Long) {
                barProgress += tickIncrease
                splashBinding.clLoadingBarWrapper.setPadding(fiveDPAsPixels, fiveDPAsPixels, max(dpToPixels(lerp(startMargin, endMargin, barProgress), scale), fiveDPAsPixels), fiveDPAsPixels)

//                Log.d("progress", barProgress.toString())
            }

            override fun onFinish() {
                startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                finish()
            }
        }.start()


    }

    fun dpToPixels(inputDP: Int, scale: Float): Int{
        return (inputDP * scale).roundToInt()
    }
}