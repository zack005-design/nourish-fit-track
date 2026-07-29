package com.fitnessapp.data.repository

import android.content.SharedPreferences

class SettingsRepository(private val sharedPreferences: SharedPreferences) {

    companion object {
        private const val KEY_DAILY_CALORIE_TARGET = "daily_calorie_target"
        private const val KEY_SLEEP_TARGET_HOURS = "sleep_target_hours"

        private const val DEFAULT_DAILY_CALORIE_TARGET = 2000
        private const val DEFAULT_SLEEP_TARGET_HOURS = 8.0f
    }

    fun getDailyCalorieTarget(): Int {
        return sharedPreferences.getInt(KEY_DAILY_CALORIE_TARGET, DEFAULT_DAILY_CALORIE_TARGET)
    }

    fun setDailyCalorieTarget(target: Int) {
        sharedPreferences.edit().putInt(KEY_DAILY_CALORIE_TARGET, target).apply()
    }

    fun getSleepTargetHours(): Float {
        return sharedPreferences.getFloat(KEY_SLEEP_TARGET_HOURS, DEFAULT_SLEEP_TARGET_HOURS)
    }

    fun setSleepTargetHours(target: Float) {
        sharedPreferences.edit().putFloat(KEY_SLEEP_TARGET_HOURS, target).apply()
    }
}
