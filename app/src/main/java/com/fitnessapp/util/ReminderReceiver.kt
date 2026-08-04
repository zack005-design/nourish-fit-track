package com.fitnessapp.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * BroadcastReceiver for handling scheduled AlarmManager intents and firing push notifications.
 */
class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra(EXTRA_REMINDER_TYPE) ?: TYPE_HYDRATION
        when (type) {
            TYPE_BREAKFAST -> {
                ReminderNotificationHelper.sendReminderNotification(
                    context,
                    "Breakfast & Energy Check-in 🍳",
                    "Start your day strong! Log your breakfast and kickstart your daily macros."
                )
            }
            TYPE_HYDRATION -> {
                ReminderNotificationHelper.sendReminderNotification(
                    context,
                    "Hydration Reminder 💧",
                    "Time for a water break! Log 250ml to stay on top of your daily goal."
                )
            }
            TYPE_SLEEP -> {
                ReminderNotificationHelper.sendReminderNotification(
                    context,
                    "Evening Wind-Down & Sleep 🌙",
                    "Preparing for bed? Set your sleep goal or activate Night Mode tracking."
                )
            }
            else -> {
                ReminderNotificationHelper.sendReminderNotification(
                    context,
                    "Nourish Health Reminder 💚",
                    "Keep up your daily health streak!"
                )
            }
        }
    }

    companion object {
        const val EXTRA_REMINDER_TYPE = "reminder_type"
        const val TYPE_BREAKFAST = "breakfast"
        const val TYPE_HYDRATION = "hydration"
        const val TYPE_SLEEP = "sleep"
    }
}
