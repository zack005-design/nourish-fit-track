package com.fitnessapp.data.repository

import com.fitnessapp.data.db.dao.UserGoalsDao
import com.fitnessapp.data.db.entity.UserGoals
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(
    private val userGoalsDao: UserGoalsDao,
    private val context: Context? = null,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    private val prefs = context?.getSharedPreferences("nourish_settings_prefs", Context.MODE_PRIVATE)
    private val _themeMode = MutableStateFlow(prefs?.getString("theme_mode", "OBSIDIAN") ?: "OBSIDIAN")
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    val userGoals: Flow<UserGoals> = userGoalsDao.getUserGoals().map { goals ->
        goals ?: UserGoals()
    }

    suspend fun saveUserGoals(userGoals: UserGoals): Long = withContext(ioDispatcher) {
        userGoalsDao.insert(userGoals.copy(id = 1))
    }

    suspend fun setThemeMode(mode: String) = withContext(ioDispatcher) {
        prefs?.edit()?.putString("theme_mode", mode)?.apply()
        _themeMode.value = mode
    }
}
