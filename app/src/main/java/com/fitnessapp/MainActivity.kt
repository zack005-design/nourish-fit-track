package com.fitnessapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.fitnessapp.ui.navigation.FitnessNavGraph
import com.fitnessapp.ui.theme.FitnessTheme

import androidx.activity.SystemBarStyle

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )


        val app = application as FitnessApp

        setContent {
            FitnessTheme {
                FitnessNavGraph(
                    foodRepository = app.foodRepository,
                    sleepRepository = app.sleepRepository,
                    waterRepository = app.waterRepository,
                    stepsRepository = app.stepsRepository,
                    settingsRepository = app.settingsRepository
                )
            }
        }
    }
}
