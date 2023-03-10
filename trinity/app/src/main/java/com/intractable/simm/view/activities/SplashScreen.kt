package com.intractable.simm.view.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.intractable.simm.utils.AppUtil
import com.intractable.simm.databinding.ActivitySplashScreenBinding
import com.intractable.simm.viewmodel.SplashScreenViewModel

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    lateinit var appUtil: AppUtil
    private lateinit var splashBinding: ActivitySplashScreenBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appUtil = AppUtil(this)
        appUtil.setDarkMode()
        splashBinding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(splashBinding.root)


        val loadTime: Long = 3500
        val timePerFrame: Long = 16
        val tickIncrease = timePerFrame.toFloat() / loadTime.toFloat() * 120f  // 120 because there seems to be some rounding errors when adding

        var barProgress = 0f
        splashBinding.pbProgressBar.progress = barProgress.toInt()

        val splashViewModel = ViewModelProvider(this)[SplashScreenViewModel::class.java]

        splashViewModel.splashScreenImage.observe(this){
            splashBinding.ivSplashImage.setImageResource(it!!.splashScreenImage)
        }

        splashViewModel.splashScreenComment.observe(this){
            splashBinding.tvSplashComment.text=it!!.splashScreenComment
        }

        object : CountDownTimer(loadTime, timePerFrame) {

            override fun onTick(millisUntilFinished: Long) {
                barProgress += tickIncrease
                splashBinding.pbProgressBar.progress = barProgress.toInt()
                Log.d("progress", barProgress.toString())
            }

            override fun onFinish() {
                startActivity(Intent(this@SplashScreen, SessionActivity::class.java))
                finish()
            }
        }.start()


    }
}