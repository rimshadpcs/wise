package com.intractable.simm

import android.app.Application
import androidx.work.*
import com.intractable.simm.gamelogic.Config
import com.intractable.simm.services.DailyNotificationsManager
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val config = Config.instance
        config.initStorageManager(this)

        //Start daily notification handling in background.

        //val notificationWorker: WorkRequest = OneTimeWorkRequestBuilder<DailyNotificationsManager>().build()

        val notificationWorker = PeriodicWorkRequestBuilder<DailyNotificationsManager>(
            PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
            TimeUnit.MILLISECONDS,
            PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS,
            TimeUnit.MILLISECONDS
        ).build()

        WorkManager
            .getInstance(this)
            .enqueueUniquePeriodicWork("periodic_handling", ExistingPeriodicWorkPolicy.KEEP, notificationWorker)
    }

}