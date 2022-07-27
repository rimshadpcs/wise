package com.example.simmone.view.activities

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.simmone.databinding.ActivityEndSessionBinding
import com.example.simmone.ProgressManager
import com.example.simmone.R
import com.example.simmone.utils.AppUtil


class EndSessionActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityEndSessionBinding
    lateinit var appUtil: AppUtil

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        viewBinding = ActivityEndSessionBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        appUtil = AppUtil(this)
        appUtil.setDarkMode()

        viewBinding.btFinishSession.setOnClickListener {
            ProgressManager.instance.saveProgress(this)

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        val mp : MediaPlayer = MediaPlayer.create(this, R.raw.session_complete)
        if(mp.isPlaying) {
            mp.pause() // Pause the current track
        }
        mp.start()  // play sound

        // delay for button showing
        // not sure if this is the best way to do it
        viewBinding.btFinishSession.visibility = View.INVISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            viewBinding.btFinishSession.visibility = View.VISIBLE
        }, 2000)
    }
}