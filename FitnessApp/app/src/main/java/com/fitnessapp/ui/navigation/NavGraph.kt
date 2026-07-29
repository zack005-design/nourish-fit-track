package com.fitnessapp.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.outlined.Bed
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.fitnessapp.data.repository.FoodRepository
import com.fitnessapp.data.repository.SettingsRepository
import com.fitnessapp.data.repository.SleepRepository
import com.fitnessapp.data.repository.WorkoutRepository
import com.fitnessapp.ui.screens.food.AddFoodScreen
import com.fitnessapp.ui.screens.food.FoodLogScreen
import com.fitnessapp.ui.screens.home.HomeScreen
import com.fitnessapp.ui.screens.settings.SettingsScreen
import com.fitnessapp.ui.screens.sleep.AddSleepScreen
import com.fitnessapp.ui.screens.sleep.SleepLogScreen
import com.fitnessapp.ui.screens.workout.AddWorkoutScreen
import com.fitnessapp.ui.screens.workout.WorkoutLogScreen

sealed class Screen(val route: String, val label: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    data object Home : Screen("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    data object Food : Screen("food", "Food", Icons.Filled.Restaurant, Icons.Outlined.Restaurant)
    data object Workout : Screen("workout", "Workout", Icons.Filled.FitnessCenter, Icons.Outlined.FitnessCenter)
    data object Sleep : Screen("sleep", "Sleep", Icons.Filled.Bed, Icons.Outlined.Bed)
    data object AddFood : Screen("add_food", "Add Food", Icons.Filled.Restaurant, Icons.Outlined.Restaurant)
    data object AddWorkout : Screen("add_workout", "Add Workout", Icons.Filled.FitnessCenter, Icons.Outlined.FitnessCenter)
    data object AddSleep : Screen("add_sleep", "Add Sleep", Icons.Filled.Bed, Icons.Outlined.Bed)
    data object Settings : Screen("settings", "Settings", Icons.Filled.Settings, Icons.Filled.Settings)
}

val bottomNavItems = listOf(Screen.Home, Screen.Food, Screen.Workout, Screen.Sleep)

@Composable
fun FitnessNavGraph(
    foodRepository: FoodRepository,
    workoutRepository: WorkoutRepository,
    sleepRepository: SleepRepository,
    settingsRepository: SettingsRepository
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.route in bottomNavItems.map { it.route }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val onShowSnackbar: (String) -> Unit = { message ->
        scope.launch { snackbarHostState.showSnackbar(message) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                                    contentDescription = screen.label
                                )
                            },
                            label = { Text(screen.label) },
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    foodRepository = foodRepository,
                    sleepRepository = sleepRepository,
                    settingsRepository = settingsRepository,
                    onNavigateToAddFood = { navController.navigate(Screen.AddFood.route) },
                    onNavigateToAddSleep = { navController.navigate(Screen.AddSleep.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }
            composable(Screen.Food.route) {
                FoodLogScreen(
                    foodRepository = foodRepository,
                    onAddClick = { navController.navigate(Screen.AddFood.route) },
                    onEditClick = { id -> navController.navigate("${Screen.AddFood.route}?id=$id") },
                    onShowSnackbar = onShowSnackbar
                )
            }
            composable(Screen.Workout.route) {
                WorkoutLogScreen(
                    workoutRepository = workoutRepository,
                    onAddClick = { navController.navigate(Screen.AddWorkout.route) },
                    onEditClick = { id -> navController.navigate("${Screen.AddWorkout.route}?id=$id") },
                    onShowSnackbar = onShowSnackbar
                )
            }
            composable(Screen.Sleep.route) {
                SleepLogScreen(
                    sleepRepository = sleepRepository,
                    onAddClick = { navController.navigate(Screen.AddSleep.route) },
                    onEditClick = { id -> navController.navigate("${Screen.AddSleep.route}?id=$id") },
                    onShowSnackbar = onShowSnackbar
                )
            }
            composable(
                route = "${Screen.AddFood.route}?id={id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType; nullable = true })
            ) { backStackEntry ->
                val idStr = backStackEntry.arguments?.getString("id")
                val id = idStr?.toLongOrNull()
                AddFoodScreen(
                    foodRepository = foodRepository,
                    entryId = id,
                    onBack = { navController.popBackStack() },
                    onShowSnackbar = onShowSnackbar
                )
            }
            composable(
                route = "${Screen.AddWorkout.route}?id={id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType; nullable = true })
            ) { backStackEntry ->
                val idStr = backStackEntry.arguments?.getString("id")
                val id = idStr?.toLongOrNull()
                AddWorkoutScreen(
                    workoutRepository = workoutRepository,
                    entryId = id,
                    onBack = { navController.popBackStack() },
                    onShowSnackbar = onShowSnackbar
                )
            }
            composable(
                route = "${Screen.AddSleep.route}?id={id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType; nullable = true })
            ) { backStackEntry ->
                val idStr = backStackEntry.arguments?.getString("id")
                val id = idStr?.toLongOrNull()
                AddSleepScreen(
                    sleepRepository = sleepRepository,
                    entryId = id,
                    onBack = { navController.popBackStack() },
                    onShowSnackbar = onShowSnackbar
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    settingsRepository = settingsRepository,
                    onBack = { navController.popBackStack() },
                    onShowSnackbar = onShowSnackbar
                )
            }
        }
    }
}
