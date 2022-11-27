package com.intractable.simm.services

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.intractable.simm.R
import com.intractable.simm.utils.Constants
import com.intractable.simm.view.activities.SessionActivity


class NotificationManager2(context: Context, workerParams: WorkerParameters):
    Worker(context, workerParams) {

    private val channelId1 = "channel_ID_1"
    private val channelId2 = "channel_ID_2"
    private val notificationID = 102
    override fun doWork(): Result{

        triggerNotify()
        return Result.success()
    }

    private fun triggerNotify() {
        createNotificationChannel()

        val notificationStyle = NotificationCompat.BigPictureStyle()

        val remotePicture = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.simm_cover)
        notificationStyle.bigPicture(remotePicture)
        val intent = Intent(applicationContext, SessionActivity::class.java)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.action = Intent.ACTION_MAIN
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)


        val pendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        var notificationTitle = ""
        var notificationContent = ""
        if (Constants.notification_session_type == 1){
            notificationTitle = applicationContext.getString(R.string.tapHere)
            notificationContent = applicationContext.getString(R.string.tapHereFull)
            val builder = NotificationCompat.Builder(applicationContext, channelId1)
                .setSmallIcon(R.drawable.simm_qs_icon2)
                .setContentTitle(notificationTitle)
                .setContentText(notificationContent)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
            with(NotificationManagerCompat.from(applicationContext)){
                notify(notificationID,builder.build())
            }
        }
        else if (Constants.notification_session_type == 2){
            notificationTitle = applicationContext.getString(R.string.hide_and_seek_not_title)
            notificationContent = applicationContext.getString(R.string.hide_and_seek_not_content)
            val builder = NotificationCompat.Builder(applicationContext, channelId2)
                .setSmallIcon(R.drawable.simm_qs_icon2)
                .setContentTitle(notificationTitle)
                .setContentText(notificationContent)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
            with(NotificationManagerCompat.from(applicationContext)){
                notify(notificationID,builder.build())
            }
        }
        else if (Constants.notification_session_type == 3) {
            notificationTitle = "Hey, got a minute?"
            notificationContent = "I've prepared some interesting content for today's session. Come take a look!"
            val builder = NotificationCompat.Builder(applicationContext, channelId1)
                .setSmallIcon(R.drawable.simm_qs_icon2)
                .setContentTitle(notificationTitle)
                .setContentText(notificationContent)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
            with(NotificationManagerCompat.from(applicationContext)){
                notify(notificationID,builder.build())
            }
        }

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name:String = applicationContext.getString(R.string.notification_channel)
            val descriptionText = applicationContext.getString(R.string.channel_description)
            var importance:Int? = null
            var channel:NotificationChannel? = null
            Log.e("session",Constants.notification_session_type.toString())
            if (Constants.notification_session_type == 1 || Constants.notification_session_type == 3) {
                importance = NotificationManager.IMPORTANCE_HIGH
                channel = NotificationChannel(channelId1, name, importance).apply {
                    description = descriptionText.toString()
                }
            }
            else if (Constants.notification_session_type == 2) {
                importance = NotificationManager.IMPORTANCE_MIN
                channel = NotificationChannel(channelId2, "Simm's hiding spot", importance).apply {
                    description = descriptionText.toString()
                }
            }
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel!!)
        }
    }
}