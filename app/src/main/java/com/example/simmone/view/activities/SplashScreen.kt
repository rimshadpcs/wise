package com.example.simmone.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.simmone.R
import kotlinx.coroutines.*

@DelicateCoroutinesApi
class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        GlobalScope.launch {
            delay(3000L)
            startActivity(Intent(this@SplashScreen,MainActivity::class.java))
            finish()
        }

    }
}