package com.example.simmone.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.simmone.databinding.ActivityOperationResultBinding
import com.example.simmone.viewmodel.OperationResultViewModel

class OperationResultActivity : AppCompatActivity() {

    private lateinit var operationResultBinding:ActivityOperationResultBinding
    private  val operationResultViewModel: OperationResultViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        operationResultBinding = ActivityOperationResultBinding.inflate(layoutInflater)
        setContentView(operationResultBinding.root)

        operationResultBinding.tvResult.text = operationResultViewModel.outputMessage
        operationResultBinding.btNext.text = operationResultViewModel.nextButton
        operationResultBinding.btNext.setOnClickListener{
            val intent = Intent(this,McqActivity::class.java)
            intent.putExtra("FROM", 1)
            startActivity(intent)
        }

    }
}