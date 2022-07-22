package com.example.simmone.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.simmone.dataStore.SessionManager
import com.example.simmone.databinding.ActivityEndSessionBinding
import com.example.simmone.dataStore.GoldManager
import com.example.simmone.dataStore.dataStore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class EndSessionActivity : AppCompatActivity() {
    lateinit var goldManager: GoldManager

    private lateinit var viewBinding:ActivityEndSessionBinding

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

    }
}