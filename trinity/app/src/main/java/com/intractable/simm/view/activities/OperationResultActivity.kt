package com.intractable.simm.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.intractable.simm.databinding.ActivityOperationResultBinding

import com.intractable.simm.utils.AppUtil
import com.intractable.simm.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import com.intractable.simm.viewmodel.OperationResultViewModel as OperationResultViewModel1

@AndroidEntryPoint
class OperationResultActivity : AppCompatActivity() {
//    @Inject
//    @Named("outputMessageForNotification")
//    lateinit var outputMessageForNotification: String
//    @Inject
//    @Named("nextButton")
//    lateinit var nextButton: String

    private lateinit var operationResultBinding:ActivityOperationResultBinding
    private  val operationResultViewModel: OperationResultViewModel1 by viewModels()
    lateinit var appUtil: AppUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        operationResultBinding = ActivityOperationResultBinding.inflate(layoutInflater)
        setContentView(operationResultBinding.root)
        appUtil = AppUtil(this)
        appUtil.setDarkMode()

        operationResultBinding.sessionProgress.progress = Constants.progressSession

        operationResultBinding.btnContinue.setOnClickListener {
            finish()
        }

//        operationResultBinding.tvResult.text = outputMessageForNotification
//        operationResultBinding.btNext.text = nextButton
//
//        operationResultBinding.btNext.setOnClickListener{
////            val intent = Intent(this, SessionActivity::class.java)
////            intent.putExtra("FROM", Constants.FLAG_OPERATION_RESULT)
////            startActivity(intent)
//            finish()
////            super.onBackPressed();

//        }

    }
}
