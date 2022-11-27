package com.intractable.simm.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.asLiveData
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.intractable.simm.R
import com.intractable.simm.dataStore.StorageManager
import com.intractable.simm.dataStore.dataStore
import com.intractable.simm.gamelogic.Config
import com.intractable.simm.utils.DateTimeFunctions
import com.intractable.simm.utils.Plants
import com.intractable.simm.view.activities.HomeActivity
import com.intractable.simm.view.widgets.PlantWidget
import com.intractable.simm.viewmodel.MainViewModel
import kotlin.random.Random


class DailyNotificationsManager(context: Context, workerParams: WorkerParameters):
    CoroutineWorker(context, workerParams) {
    private val channelId = "daily_reminder"
    private val notificationID = 201
    override suspend fun doWork(): Result {

        val storageManager = Config.instance.getInitializedStorageManager()!!

        Log.i("DailyNotifsManager", "obtaining sessionCount")

        //storageManager.sessionCountFlow.collect {
        //    if (it != null) {
        //        Log.i("DailyNotificationsManager", "sessionCount = " + it)
        //        triggerNotify()
        //        Log.i("DailyNotificationsManager", "finished processing sessionCount")
        //    }
        //}

        val sessionNumber = storageManager.getSessionNumber()
        if (sessionNumber != null) {
            Log.i("DailyNotifsManager", "sessionNumber = " + sessionNumber)
        }

        checkForDailyNotification()
        regenerateHeartsIfNeeded()


        Log.d("DailyNotifsManager", "background Coroutine End")
        return Result.success()
    }

    private suspend fun checkForDailyNotification(){
        val storageManager = Config.instance.storageManager!!
        val timeOfDayForNotif = 18

        // if all sessions completed, return
//        if((storageManager.getSessionNumber() ?: 0) > Config.instance.progressManager.getSessionListFlow().asLiveData().value?.size!!){
//            Log.d("dailyNotification", "all sessions done")
//            return
//        }
//        else{
//            Log.d("dailyNotification", "sessions still need to be done")
//        }

        Log.d("dailyNotification", "checking for dailyNotif. Hour: " + DateTimeFunctions.getCurrentHour().toString())
        if(DateTimeFunctions.getCurrentHour() >= timeOfDayForNotif && (storageManager.getDailyNotification() == 0 || storageManager.getDailyNotification() == null)){
            Log.d("dailyNotification", "time condition fulfilled")
            //                                                18 hours * 60 minutes * 60 seconds * 1000 ms
            if(DateTimeFunctions.getCurrentDateTimeEpochString().toLong() -
                (storageManager.getSessionTimeEpoch() ?.toLong() ?: 0) > timeOfDayForNotif * 60 * 60 * 1000){
                Log.d("dailyNotification", "no session done today")
                // send notification
                val coupleOfDays = DateTimeFunctions.getCurrentDateTimeEpochString().toLong() -
                        (storageManager.getSessionTimeEpoch() ?.toLong() ?: 0) > (timeOfDayForNotif + 24) * 60 * 60 * 1000
                triggerNotify(coupleOfDays, storageManager.getPlantGrowth()?:0)
                storageManager.storeDailyNotification(1)
            }
        }
        if(DateTimeFunctions.getCurrentHour() <= 1 && storageManager.getDailyNotification() != 0){  // reset every day
            Log.d("dailyNotification", "resetting dailyNotification")
            storageManager.storeDailyNotification(0)
        }
    }

    private fun triggerNotify(coupleOfDays: Boolean, plantGrowthStage: Int) {
        lateinit var mainModel: MainViewModel
        lateinit var firebaseAnalytics: FirebaseAnalytics

        createNotificationChannel()

        val notificationStyle = NotificationCompat.BigPictureStyle()

        val remotePicture = BitmapFactory.decodeResource(applicationContext.resources,
            R.drawable.simm_cover)
        notificationStyle.bigPicture(remotePicture)
        val intent = Intent(applicationContext, HomeActivity::class.java).apply {
            this.putExtra("cosmodrome",true)
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            flags = Intent.FLAG_ACTIVITY_NEW_TASK



        }


        val pendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        // TODO
        // get notification title and content depending on a few things:
        // If they haven't finished a session today
            // if they haven't finished a session in a couple of days
            // else if they are very close to a milestone
        // not sure how to retrieve the text

        var notificationTitle = "Daily Reminder"
        var notificationContent = "Finish your daily learning session!"

        if(coupleOfDays){
            val dailyNotifications = arrayOf(
                arrayOf(
                    "No time to waste!", "Come on, we've got a world to save!"
                ),
                arrayOf(
                    "Your plant misses you!", "It's been a few days since your plant grew. Finish a session now to see its next stage."
                ),
                arrayOf(
                    "Don't forget to keep learning!", "I haven't seen you in a while, come try out today's session. It will only take a minute."
                )
            )
            val randomInt = (dailyNotifications.indices).random()  // 0 - max, max inclusive

            notificationTitle = dailyNotifications[randomInt][0]
            notificationContent = dailyNotifications[randomInt][1]
        }
        else{
            var random = Random.Default.nextInt(4)
            if(random == 5){  // plant growth stage notification
                //
                val dailyNotifications = arrayOf(
                    arrayOf(
                        "Collect new sprouts!", "Finish a new session to collect a new plant"
                    ),
                    arrayOf(
                        "Come grow your plant!", "Complete a session to cultivate your plant!"
                    )
                )

                if(plantGrowthStage == 5){
                    notificationTitle = dailyNotifications[0][0]
                    notificationContent = dailyNotifications[0][1]
                }
                else{
                    random = Random.Default.nextInt(dailyNotifications.size - 1) + 1
                    notificationTitle = dailyNotifications[random][0]
                    notificationContent = dailyNotifications[random][1]
                }

            }
            else {  // normal daily notification
                val dailyNotifications = arrayOf(
                    arrayOf(
                        "Hey, got a minute?",
                        "I've prepared some interesting content for today's session. Come take a look!"
                    ),
                    arrayOf(
                        "Your plant is awake!", "Complete today's session to grow your plant"
                    )
                )
                val randomInt = (dailyNotifications.indices).random()  // 0 - max, max inclusive

                notificationTitle = dailyNotifications[randomInt][0]
                notificationContent = dailyNotifications[randomInt][1]
            }
        }

        firebaseAnalytics = Firebase.analytics
        firebaseAnalytics.logEvent("simm_daily_notification_sent", null)

        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.simm_qs_icon2)
            .setContentTitle(notificationTitle)
            .setContentText(notificationContent)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

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

    private suspend fun regenerateHeartsIfNeeded(){
        Log.d("DailyNotifsManager", "checking for hearts")
        val storageManager = StorageManager(applicationContext.dataStore)
        val currentTime = DateTimeFunctions.getCurrentDateTimeEpoch()
        val heartsRegenTime = (storageManager.getHeartsRegen()?:(currentTime.toString())).toLong()
        val twentyMinutesInMillis = 20 * 60 * 1000  // 20 minutes * 60 seconds * 1000 milliseconds

        if(currentTime - heartsRegenTime > twentyMinutesInMillis){
            Log.d("DailyNotifsManager","Time fulfilled")
            var currentHearts = storageManager.getHearts()?:5
            if(currentHearts < 5){
                currentHearts = 5
                storageManager.storeHearts(currentHearts)
                storageManager.storeHeartsRegen(currentTime.toString())
                Log.d("DailyNotifsManager", "Heart regened")
            }
            else{
                Log.d("DailyNotifsManager", "Hearts full")
            }
        }
    }

}

