package com.fitnessapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fitnessapp.data.db.entity.UserGoals
import kotlinx.coroutines.flow.Flow

@Dao
interface UserGoalsDao {

    @Query("SELECT * FROM user_goals WHERE id = 1 LIMIT 1")
    fun getUserGoals(): Flow<UserGoals?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userGoals: UserGoals): Long
}
