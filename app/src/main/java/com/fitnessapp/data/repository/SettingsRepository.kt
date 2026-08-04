package com.fitnessapp.data.repository

import com.fitnessapp.data.db.dao.UserGoalsDao
import com.fitnessapp.data.db.entity.UserGoals
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SettingsRepository(
    private val userGoalsDao: UserGoalsDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    val userGoals: Flow<UserGoals> = userGoalsDao.getUserGoals().map { goals ->
        goals ?: UserGoals()
    }

    suspend fun saveUserGoals(userGoals: UserGoals): Long = withContext(ioDispatcher) {
        userGoalsDao.insert(userGoals.copy(id = 1))
    }
}
