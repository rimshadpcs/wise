package com.intractable.simm.view.activities

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.intractable.simm.databinding.ActivityEndSessionBinding
import com.intractable.simm.R
import com.intractable.simm.utils.AppUtil


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
            com.intractable.simm.ProgressManager.instance.saveProgress(this)

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