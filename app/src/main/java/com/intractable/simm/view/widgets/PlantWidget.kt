package com.intractable.simm.view.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.intractable.simm.R
import com.intractable.simm.dataStore.StorageManager
import com.intractable.simm.dataStore.dataStore

/**
 * Implementation of App Widget functionality.
 */
class PlantWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {

        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
) {
    val widgetText = context.getString(R.string.appwidget_text)
    // Construct the RemoteViews object
//    val storageManager = StorageManager(applicationContext.dataStore)

    val views = RemoteViews(context.packageName, R.layout.plant_widget)
    views.setImageViewResource(R.id.iv_plant, R.drawable.tulip_purple_asleep_stage4)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}