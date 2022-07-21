package com.example.simmone.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.simmone.dataStore.SessionManager
import com.example.simmone.databinding.ActivityEndSessionBinding

class EndSessionActivity : AppCompatActivity() {

    private lateinit var viewBinding:ActivityEndSessionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityEndSessionBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.btFinishSession.setOnClickListener {
            SessionManager.instance.session_num++
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }
}