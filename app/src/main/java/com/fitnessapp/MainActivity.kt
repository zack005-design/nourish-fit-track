package com.fitnessapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.fitnessapp.ui.navigation.FitnessNavGraph
import com.fitnessapp.ui.theme.FitnessTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as FitnessApp

        setContent {
            FitnessTheme {
                FitnessNavGraph(
                    foodRepository = app.foodRepository,
                    workoutRepository = app.workoutRepository,
                    sleepRepository = app.sleepRepository,
                    settingsRepository = app.settingsRepository
                )
            }
        }
    }
}
