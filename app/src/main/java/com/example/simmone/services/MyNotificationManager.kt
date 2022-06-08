package com.example.simmone.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.simmone.R
import com.example.simmone.view.OperationResultActivity


class MyNotificationManager(context: Context, workerParams: WorkerParameters):
    Worker(context, workerParams) {
        private val channelId = "channel_ID"
        private val notificationID = 101
    override fun doWork(): Result{

    triggerNotify()
        return Result.success()
    }

    fun triggerNotify() {
        createNotificationChannel()

        val notificationStyle = NotificationCompat.BigPictureStyle()

        val remotePicture = BitmapFactory.decodeResource(applicationContext.resources,R.drawable.boy)
        notificationStyle.bigPicture(remotePicture)


        val intent = Intent(applicationContext, OperationResultActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        val notificationTitle = applicationContext.getString(R.string.tapHere)
        val notificationContent = applicationContext.getString(R.string.tapHereFull)
        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.simm_cover)
            .setContentTitle(notificationTitle.toString())
            .setContentText(notificationContent.toString())
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(applicationContext)){
            notify(notificationID,builder.build())
        }
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = R.string.notification_channel
            val descriptionText = R.string.channel_description
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name.toString(), importance).apply {
                description = descriptionText.toString()
            }
            // Register the channel with the system
            val notificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


}

