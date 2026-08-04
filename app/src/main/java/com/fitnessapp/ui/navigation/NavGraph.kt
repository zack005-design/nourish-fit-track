package com.fitnessapp.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fitnessapp.data.repository.FoodRepository
import com.fitnessapp.data.repository.SettingsRepository
import com.fitnessapp.data.repository.SleepRepository
import com.fitnessapp.data.repository.StepsRepository
import com.fitnessapp.data.repository.WaterRepository
import com.fitnessapp.ui.components.frostedGlass
import com.fitnessapp.ui.screens.analytics.AnalyticsScreen
import com.fitnessapp.ui.screens.food.AddFoodScreen
import com.fitnessapp.ui.screens.food.FoodLogScreen
import com.fitnessapp.ui.screens.food.NutritionDetailsScreen
import com.fitnessapp.ui.screens.home.HomeScreen
import com.fitnessapp.ui.screens.settings.SettingsScreen
import com.fitnessapp.ui.screens.sleep.AddSleepScreen
import com.fitnessapp.ui.screens.sleep.SleepLogScreen
import com.fitnessapp.ui.screens.splash.SplashScreen
import com.fitnessapp.ui.theme.AccentGreen
import com.fitnessapp.ui.theme.BackgroundDark
import com.fitnessapp.ui.theme.SurfaceCard
import com.fitnessapp.ui.theme.TextTertiary
import kotlinx.coroutines.launch

sealed class Screen(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Splash : Screen("splash", "Splash", Icons.Filled.GridView, Icons.Outlined.GridView)
    data object Overview : Screen("overview", "Overview", Icons.Filled.GridView, Icons.Outlined.GridView)
    data object Nutrition : Screen("nutrition", "Nutrition", Icons.Filled.Restaurant, Icons.Outlined.Restaurant)
    data object NutritionDetails : Screen("nutrition_details", "Nutrition Details", Icons.Filled.Restaurant, Icons.Outlined.Restaurant)
    data object Sleep : Screen("sleep", "Sleep", Icons.Filled.Bedtime, Icons.Outlined.Bedtime)
    data object Analytics : Screen("analytics", "Analytics", Icons.Filled.BarChart, Icons.Outlined.BarChart)
    data object More : Screen("more", "More", Icons.Filled.Menu, Icons.Outlined.Menu)
    data object AddFood : Screen("add_food", "Add Food", Icons.Filled.Restaurant, Icons.Outlined.Restaurant)
    data object AddSleep : Screen("add_sleep", "Add Sleep", Icons.Filled.Bedtime, Icons.Outlined.Bedtime)
}

val bottomNavItems = listOf(
    Screen.Overview,
    Screen.Nutrition,
    Screen.Sleep,
    Screen.Analytics,
    Screen.More
)

@Composable
fun FitnessNavGraph(
    foodRepository: FoodRepository,
    sleepRepository: SleepRepository,
    waterRepository: WaterRepository,
    stepsRepository: StepsRepository,
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
        containerColor = BackgroundDark,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    modifier = Modifier.frostedGlass(
                        backgroundColor = SurfaceCard.copy(alpha = 0.85f),
                        fallbackColor = SurfaceCard
                    ),
                    containerColor = Color.Transparent
                ) {
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
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AccentGreen,
                                selectedTextColor = AccentGreen,
                                indicatorColor = AccentGreen.copy(alpha = 0.15f),
                                unselectedIconColor = TextTertiary,
                                unselectedTextColor = TextTertiary
                            ),
                            onClick = {
                                if (currentDestination?.route != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
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
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    onSplashFinished = {
                        navController.navigate(Screen.Overview.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Overview.route) {
                HomeScreen(
                    foodRepository = foodRepository,
                    sleepRepository = sleepRepository,
                    waterRepository = waterRepository,
                    stepsRepository = stepsRepository,
                    settingsRepository = settingsRepository,
                    onNavigateToFoodLog = { navController.navigate(Screen.Nutrition.route) },
                    onNavigateToSleepLog = { navController.navigate(Screen.Sleep.route) },
                    onNavigateToSettings = { navController.navigate(Screen.More.route) }
                )
            }

            composable(Screen.Nutrition.route) {
                FoodLogScreen(
                    foodRepository = foodRepository,
                    onAddClick = { navController.navigate(Screen.AddFood.route) },
                    onEditClick = { id -> navController.navigate("${Screen.AddFood.route}?id=$id") },
                    onNavigateToDetails = { navController.navigate(Screen.NutritionDetails.route) },
                    onShowSnackbar = onShowSnackbar
                )
            }

            composable(Screen.NutritionDetails.route) {
                NutritionDetailsScreen(
                    foodRepository = foodRepository,
                    onBack = { navController.popBackStack() },
                    onAddFoodClick = { navController.navigate(Screen.AddFood.route) },
                    onEditEntryClick = { id -> navController.navigate("${Screen.AddFood.route}?id=$id") }
                )
            }

            composable(Screen.Sleep.route) {
                SleepLogScreen(
                    sleepRepository = sleepRepository,
                    settingsRepository = settingsRepository,
                    onAddClick = { navController.navigate(Screen.AddSleep.route) },
                    onEditClick = { id -> navController.navigate("${Screen.AddSleep.route}?id=$id") },
                    onShowSnackbar = onShowSnackbar
                )
            }

            composable(Screen.Analytics.route) {
                AnalyticsScreen(
                    foodRepository = foodRepository,
                    waterRepository = waterRepository,
                    sleepRepository = sleepRepository,
                    settingsRepository = settingsRepository,
                    stepsRepository = stepsRepository
                )
            }

            composable(Screen.More.route) {
                SettingsScreen(
                    settingsRepository = settingsRepository,
                    foodRepository = foodRepository,
                    waterRepository = waterRepository,
                    sleepRepository = sleepRepository,
                    stepsRepository = stepsRepository,
                    onBack = { navController.popBackStack() },
                    onShowSnackbar = onShowSnackbar
                )
            }

            composable(
                route = "${Screen.AddFood.route}?id={id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType; nullable = true; defaultValue = null })
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
                route = "${Screen.AddSleep.route}?id={id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType; nullable = true; defaultValue = null })
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
        }
    }
}
