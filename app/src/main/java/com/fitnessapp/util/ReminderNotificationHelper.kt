package com.fitnessapp.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.fitnessapp.R

/**
 * Handles local push notifications for hydration, meal reminders, and daily health goals.
 * No external API required — 100% on-device.
 */
object ReminderNotificationHelper {

    private const val CHANNEL_ID = "nourish_reminders_channel"
    private const val CHANNEL_NAME = "Daily Reminders & Health Tips"

    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Daily hydration, meal logging, and recovery reminders"
        }
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun sendReminderNotification(context: Context, title: String, message: String) {
        createNotificationChannel(context)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }

    /**
     * Schedules daily background alarm reminders for Breakfast (8 AM), Hydration (2 PM), and Sleep (10 PM).
     */
    fun scheduleDailyReminders(context: Context) {
        scheduleAlarm(context, ReminderReceiver.TYPE_BREAKFAST, 8, 0, 1001)
        scheduleAlarm(context, ReminderReceiver.TYPE_HYDRATION, 14, 0, 1002)
        scheduleAlarm(context, ReminderReceiver.TYPE_SLEEP, 22, 0, 1003)
    }

    private fun scheduleAlarm(context: Context, type: String, hour: Int, minute: Int, requestCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? android.app.AlarmManager ?: return
        val intent = android.content.Intent(context, ReminderReceiver::class.java).apply {
            putExtra(ReminderReceiver.EXTRA_REMINDER_TYPE, type)
        }
        val pendingIntent = android.app.PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, hour)
            set(java.util.Calendar.MINUTE, minute)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
            if (before(java.util.Calendar.getInstance())) {
                add(java.util.Calendar.DAY_OF_YEAR, 1)
            }
        }

        try {
            alarmManager.setInexactRepeating(
                android.app.AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                android.app.AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        } catch (e: Exception) {
            // Ignore security exception if exact alarms disallowed
        }
    }
}

