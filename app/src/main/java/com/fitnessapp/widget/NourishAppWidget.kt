package com.fitnessapp.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.fitnessapp.MainActivity
import com.fitnessapp.R
import com.fitnessapp.data.db.FitnessDatabase
import com.fitnessapp.util.DateUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * Android Home Screen Widget for Nourish App.
 * Displays today's Calorie, Water, and Step progress right on the user's home screen.
 */
class NourishAppWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        updateAllWidgets(context, appWidgetManager, appWidgetIds)
    }

    companion object {
        fun updateAllWidgets(context: Context, appWidgetManager: AppWidgetManager? = null, appWidgetIds: IntArray? = null) {
            val manager = appWidgetManager ?: AppWidgetManager.getInstance(context)
            val ids = appWidgetIds ?: manager.getAppWidgetIds(ComponentName(context, NourishAppWidget::class.java))
            if (ids.isEmpty()) return

            CoroutineScope(Dispatchers.IO).launch {
                val db = FitnessDatabase.getInstance(context)
                val todayStart = DateUtils.todayStartMillis()
                val todayEnd = DateUtils.endOfDayMillis(todayStart)

                val foodEntries = db.foodEntryDao().getEntriesForDateRange(todayStart, todayEnd).firstOrNull() ?: emptyList()
                val calories = foodEntries.sumOf { it.calories }

                val waterEntries = db.waterEntryDao().getEntriesForDateRange(todayStart, todayEnd).firstOrNull() ?: emptyList()
                val waterMl = waterEntries.sumOf { it.amountMl }
                val waterL = waterMl / 1000f

                val stepsEntry = db.stepsEntryDao().getStepsForDateRange(todayStart, todayEnd).firstOrNull()
                val steps = stepsEntry?.count ?: 0

                val openIntent = Intent(context, MainActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    openIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                for (appWidgetId in ids) {
                    val views = RemoteViews(context.packageName, R.layout.nourish_widget_layout)
                    views.setTextViewText(R.id.widget_calories_text, String.format(Locale.US, "%,d", calories))
                    views.setTextViewText(R.id.widget_water_text, String.format(Locale.US, "%.1f L", waterL))
                    views.setTextViewText(R.id.widget_steps_text, String.format(Locale.US, "%,d", steps))
                    views.setOnClickPendingIntent(R.id.widget_calories_text, pendingIntent)

                    manager.updateAppWidget(appWidgetId, views)
                }
            }
        }
    }
}
