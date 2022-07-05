package com.example.simmone.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.simmone.databinding.ActivityOperationResultBinding
import com.example.simmone.viewmodel.OperationResultViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class OperationResultActivity : AppCompatActivity() {
    @Inject
    @Named("outputMessageForNotification")
    lateinit var outputMessageForNotification: String
    @Inject
    @Named("nextButton")
    lateinit var nextButton: String

    private lateinit var operationResultBinding:ActivityOperationResultBinding
    private  val operationResultViewModel: OperationResultViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        operationResultBinding = ActivityOperationResultBinding.inflate(layoutInflater)
        setContentView(operationResultBinding.root)

        operationResultBinding.tvResult.text = outputMessageForNotification
        operationResultBinding.btNext.text = nextButton

        operationResultBinding.btNext.setOnClickListener{
            val intent = Intent(this, McqActivity::class.java)
            intent.putExtra("FROM", 1)
            startActivity(intent)
            finish()
//            super.onBackPressed();

        }

    }
}