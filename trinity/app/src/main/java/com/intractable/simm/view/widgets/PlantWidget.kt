package com.intractable.simm.view.widgets

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.intractable.simm.R
import com.intractable.simm.utils.Constants
import com.intractable.simm.view.activities.SettingsActivity


const val CHECK_ENABLED = "CHECK_ENABLED"
/**
 * Implementation of App Widget functionality.
 */
@Suppress("NAME_SHADOWING")
class PlantWidget : AppWidgetProvider() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        //super.onUpdate(context, appWidgetManager, appWidgetIds)
//
//        for(i in 1 until  appWidgetIds.size){
         val appWidgetIds = appWidgetIds
            val intent = Intent(context, SettingsActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

            val views = RemoteViews(context.packageName, R.layout.plant_widget)
            views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)
            appWidgetManager.updateAppWidget(appWidgetIds, views)
            super.onUpdate(context,appWidgetManager,appWidgetIds)

        firebaseAnalytics = Firebase.analytics
        val params = Bundle()
        params.putString("widget_clicked", "")
        firebaseAnalytics.logEvent("WIDGET_CLICK",params)

        //}
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
        Constants.WidgetsInstalled = true

        firebaseAnalytics = Firebase.analytics
        val params = Bundle()
        params.putString("widget_added", "")
        firebaseAnalytics.logEvent("WIDGET_ENABLED",params)
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
        Constants.WidgetsInstalled = false
        firebaseAnalytics = Firebase.analytics
        val params = Bundle()
        params.putString("widget_removed", "")
        firebaseAnalytics.logEvent("WIDGET_DISABLED",params)

    }

    @SuppressLint("UnsafeProtectedBroadcastReceiver", "UnspecifiedImmutableFlag")
    override fun onReceive(context: Context?, intent: Intent?) {
        val widgetMgr = AppWidgetManager.getInstance(context)
        val appWidgetId = Int
        widgetMgr.getAppWidgetIds(
            ComponentName(
                context!!,
                PlantWidget::class.java
            )
        )
        Constants.WidgetsInstalled = true
    }

}

@SuppressLint("UnspecifiedImmutableFlag")
internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
) {
    context.getString(R.string.appwidget_text)
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.plant_widget)


    // Instruct the widget manager to update the widget

}
