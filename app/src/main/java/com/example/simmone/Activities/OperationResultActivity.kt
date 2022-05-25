package com.example.simmone.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.simmone.R
import com.example.simmone.databinding.ActivityOperationResultBinding

class OperationResultActivity : AppCompatActivity() {

    private lateinit var operationResultBinding:ActivityOperationResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        operationResultBinding = ActivityOperationResultBinding.inflate(layoutInflater)
        setContentView(operationResultBinding.root)
        
        operationResultBinding.btNext.setOnClickListener{
            val intent = Intent(this,McqActivity::class.java)
            startActivity(intent)
        }

    }
}