package com.example.simmone.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.simmone.R
import com.example.simmone.databinding.ActivityOperationBinding


class OperationActivity : AppCompatActivity() {
    private val CHANNEL_ID = "CHANNEL_ID"
    private val notificationId = 101

    private lateinit var operationBinding: ActivityOperationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.createNotificationChannel()
        operationBinding = ActivityOperationBinding.inflate(layoutInflater)
        setContentView(operationBinding.root)

        /* button click to send notification*/
        operationBinding.btSend.setOnClickListener {
            operationBinding.tvNotificationSend.text = getText(R.string.notificationSentChangeText)
            this.sendNotificationToDevice()
        }
    }
    /* The notification part for learning session 1 STARTS here*/
    //notification channel required by newer versions
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotificationToDevice() {
        val intent = Intent(this,OperationResultActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getActivity(this,0, intent,FLAG_IMMUTABLE)
        } else {
            getActivity(this,0,intent, FLAG_UPDATE_CURRENT)
        }
        BitmapFactory.decodeResource(applicationContext.resources, R.drawable.boy)

        val notificationTitle = resources.getString(R.string.tapHere)
        val notificationContent = resources.getString(R.string.tapHereFull)
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.boy)
            .setContentTitle(notificationTitle)
            .setContentText(notificationContent)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, builder.build())
        }
    }
    /* The notification part for learning session 1 ENDS here*/

}