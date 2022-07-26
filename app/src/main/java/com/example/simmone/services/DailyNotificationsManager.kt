package com.example.simmone.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.asLiveData
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.simmone.R
import com.example.simmone.dataStore.StorageManager
import com.example.simmone.dataStore.dataStore
import com.example.simmone.view.activities.MainActivity
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.coroutineContext

class DailyNotificationsManager(context: Context, workerParams: WorkerParameters):
    CoroutineWorker(context, workerParams) {
    private val channelId = "daily_reminder"
    private val notificationID = 201
    override suspend fun doWork(): Result{

        val storageManager = StorageManager(applicationContext.dataStore)

        Log.i("DailyNotificationsManager", "obtaining sessionCount")

        //storageManager.sessionCountFlow.collect {
        //    if (it != null) {
        //        Log.i("DailyNotificationsManager", "sessionCount = " + it)
        //        triggerNotify()
        //        Log.i("DailyNotificationsManager", "finished processing sessionCount")
        //    }
        //}

        val sessionNumber = storageManager.getSessionNumber()
        if (sessionNumber != null) {
            Log.i("DailyNotificationsManager", "sessionNumber = " + sessionNumber)
            triggerNotify()
            Log.i("DailyNotificationsManager", "finished processing sessionNumber")
        }

        return Result.success()
    }

    private fun triggerNotify() {

        createNotificationChannel()

        val notificationStyle = NotificationCompat.BigPictureStyle()

        val remotePicture = BitmapFactory.decodeResource(applicationContext.resources,R.drawable.simm_cover)
        notificationStyle.bigPicture(remotePicture)
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            flags = Intent.FLAG_ACTIVITY_NEW_TASK

        }


        val pendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        val notificationTitle = "Daily Reminder"
        val notificationContent = "Finish your daily learning session!"
        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.simm_cover)
            .setContentTitle(notificationTitle)
            .setContentText(notificationContent)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(applicationContext)){
            notify(notificationID,builder.build())
        }
    }

    private fun createNotificationChannel() {
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

