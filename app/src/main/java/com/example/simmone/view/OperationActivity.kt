package com.example.simmone.view
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import com.example.simmone.R
import com.example.simmone.databinding.ActivityOperationBinding
import com.example.simmone.services.MyNotificationManager



class OperationActivity : AppCompatActivity() {

    private lateinit var operationBinding: ActivityOperationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        operationBinding = ActivityOperationBinding.inflate(layoutInflater)
        setContentView(operationBinding.root)

        /* button click to send notification*/
        operationBinding.btSend.setOnClickListener {
            operationBinding.tvNotificationSend.text = getText(R.string.notificationSentChangeText)
            val notificationWorker: WorkRequest = OneTimeWorkRequestBuilder<MyNotificationManager>().build()
            WorkManager
                .getInstance(this)
                .enqueue(notificationWorker)
        }

    }

}