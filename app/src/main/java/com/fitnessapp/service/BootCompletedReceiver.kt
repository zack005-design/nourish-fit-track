package com.fitnessapp.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * BootCompletedReceiver
 * Auto-starts the background hardware StepTrackingService on device boot completion.
 */
class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val serviceIntent = Intent(context, StepTrackingService::class.java)
            context.startForegroundService(serviceIntent)
        }
    }
}
