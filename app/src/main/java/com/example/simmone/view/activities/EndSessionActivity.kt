package com.example.simmone.view.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.simmone.R
import com.example.simmone.dataStore.GoldManager
import com.example.simmone.dataStore.SessionManager
import com.example.simmone.dataStore.dataStore
import com.example.simmone.databinding.ActivityEndSessionBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class EndSessionActivity : AppCompatActivity() {
    private lateinit var goldManager: GoldManager

    private lateinit var viewBinding:ActivityEndSessionBinding

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        goldManager = GoldManager(dataStore)

        super.onCreate(savedInstanceState)

        viewBinding = ActivityEndSessionBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.btFinishSession.setOnClickListener {
            SessionManager.instance.session_num++

            GlobalScope.launch {
                goldManager.storeGold()
            }

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // loads gif
        Glide.with(this)
            .load(R.drawable.celebration_jump)
            .into(viewBinding.ivSimmFinish)

        // delay for button showing
        // not sure if this is the best way to do it
        viewBinding.btFinishSession.visibility = View.INVISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            viewBinding.btFinishSession.visibility = View.VISIBLE
        }, 2000)
    }
}