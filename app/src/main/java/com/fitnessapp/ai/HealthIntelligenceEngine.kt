package com.fitnessapp.ai

import com.fitnessapp.data.db.entity.FoodEntry
import com.fitnessapp.data.db.entity.SleepEntry
import com.fitnessapp.data.db.entity.UserGoals
import com.fitnessapp.data.db.entity.WaterEntry
import com.fitnessapp.util.DateUtils

/**
 * Severity level for insight cards.
 * Used to color-code cards (red = critical, amber = warning, green = positive).
 */
enum class InsightSeverity { CRITICAL, WARNING, POSITIVE, INFO }

/**
 * A single AI insight card — mirrors what Whoop / Google Health surface
 * (a title, a detail paragraph, and a color-coded severity level).
 */
data class InsightCard(
    val title: String,
    val body: String,
    val severity: InsightSeverity,
    val emoji: String
)

/**
 * Full output of HealthIntelligenceEngine for the AI Coach screen.
 */
data class AiCoachReport(
    val overallScore: Int = 0,
    val nutritionScore: Int = 0,
    val hydrationScore: Int = 0,
    val sleepScore: Int = 0,
    val activityScore: Int = 0,
    val scoreDelta: Int = 0,         // vs yesterday's overall score
    val scoreLabel: String = "Good", // e.g., "Excellent", "Good", "Fair", "Poor"
    val insightCards: List<InsightCard> = emptyList(),
    val actionPlan: List<String> = emptyList(),
    val weeklySummary: String = "",
    val hasData: Boolean = false
)

/**
 * On-device health intelligence engine.
 *
 * Computes deterministic health scores and actionable insights from the
 * user's logged nutrition, hydration, sleep, and activity data — exactly as
 * Whoop, Google Health, and Garmin do (no external LLM needed).
 *
 * Call [buildReport] with the full historical data and goals to get a report.
 */
object HealthIntelligenceEngine {

    /**
     * Builds a comprehensive [AiCoachReport] from raw historical data.
     *
     * @param foodEntries All food entries from the DB.
     * @param waterEntries All water entries from the DB.
     * @param sleepEntries All sleep entries from the DB.
     * @param goals The user's configured daily goals.
     */
    fun buildReport(
        foodEntries: List<FoodEntry>,
        waterEntries: List<WaterEntry>,
        sleepEntries: List<SleepEntry>,
        goals: UserGoals
    ): AiCoachReport {
        val todayStart = DateUtils.todayStartMillis()
        val yesterdayStart = todayStart - 86_400_000L

        // ── Today's data ──────────────────────────────────────────────────────
        val todayFood = foodEntries.filter { it.dateMillis >= todayStart }
        val todayWater = waterEntries.filter { it.dateMillis >= todayStart }
        val todaySleep = sleepEntries.filter { it.dateMillis >= todayStart }.firstOrNull()

        val todayCalories = todayFood.sumOf { it.calories }
        val todayProtein = todayFood.sumOf { it.proteinGrams.toDouble() }.toFloat()
        val todayCarbs = todayFood.sumOf { it.carbsGrams.toDouble() }.toFloat()
        val todayFat = todayFood.sumOf { it.fatGrams.toDouble() }.toFloat()
        val todayFiber = todayFood.sumOf { it.fiberGrams.toDouble() }.toFloat()
        val todayWaterMl = todayWater.sumOf { it.amountMl }
        val todaySleepHours = todaySleep?.let {
            (it.endMillis - it.startMillis) / (1000f * 60f * 60f)
        } ?: 0f

        val hasData = todayCalories > 0 || todayWaterMl > 0 || todaySleep != null

        // ── 7-day window data ─────────────────────────────────────────────────
        val weekStart = todayStart - 6 * 86_400_000L
        val weekFood = foodEntries.filter { it.dateMillis >= weekStart }
        val weekWater = waterEntries.filter { it.dateMillis >= weekStart }
        val weekSleep = sleepEntries.filter { it.dateMillis >= weekStart }

        // Per-day breakdowns for 7 days
        val dailyData = (6 downTo 0).map { daysAgo ->
            val dayStart = todayStart - daysAgo * 86_400_000L
            val dayEnd = dayStart + 86_400_000L - 1
            DayData(
                dayStart = dayStart,
                calories = weekFood.filter { it.dateMillis in dayStart..dayEnd }.sumOf { it.calories },
                protein = weekFood.filter { it.dateMillis in dayStart..dayEnd }.sumOf { it.proteinGrams.toDouble() }.toFloat(),
                waterMl = weekWater.filter { it.dateMillis in dayStart..dayEnd }.sumOf { it.amountMl },
                sleepHours = weekSleep.find { it.dateMillis in dayStart..dayEnd }?.let {
                    (it.endMillis - it.startMillis) / (1000f * 60f * 60f)
                } ?: 0f,
                sleepQuality = weekSleep.find { it.dateMillis in dayStart..dayEnd }?.quality ?: 0
            )
        }

        // Yesterday for delta
        val yesterdayData = dailyData.getOrNull(5) // index 5 = yesterday

        // ── Scores (0–100) ────────────────────────────────────────────────────
        val nutritionScore = computeNutritionScore(todayCalories, todayProtein, todayFiber, goals)
        val hydrationScore = computeHydrationScore(todayWaterMl, goals.dailyWaterGoal)
        val sleepScore = computeSleepScore(todaySleepHours, todaySleep?.quality ?: 0, goals.dailySleepGoalHours)
        val activityScore = 100

        val overallScore = if (!hasData) 0 else (nutritionScore * 0.40 + hydrationScore * 0.30 + sleepScore * 0.30).toInt()

        // Yesterday overall score for delta
        val yNutritionScore = yesterdayData?.let {
            computeNutritionScore(it.calories, it.protein, 0f, goals)
        } ?: 0
        val yHydrationScore = yesterdayData?.let {
            computeHydrationScore(it.waterMl, goals.dailyWaterGoal)
        } ?: 0
        val ySleepScore = yesterdayData?.let {
            computeSleepScore(it.sleepHours, it.sleepQuality, goals.dailySleepGoalHours)
        } ?: 0
        val yOverallScore = (yNutritionScore * 0.40 + yHydrationScore * 0.30 + ySleepScore * 0.30).toInt()
        val scoreDelta = overallScore - yOverallScore

        // ── Insight Cards ─────────────────────────────────────────────────────
        val insights = mutableListOf<InsightCard>()

        // Sleep debt detection (3+ consecutive days under goal)
        val sleepGoal = goals.dailySleepGoalHours
        val lowSleepDays = dailyData.takeLast(5).count { it.sleepHours in 0.1f..sleepGoal - 0.5f }
        if (lowSleepDays >= 3) {
            insights += InsightCard(
                title = "Sleep Debt Accumulating",
                body = "You've had under ${sleepGoal.toInt()}h of sleep for $lowSleepDays of the last 5 days. Chronic sleep debt reduces muscle recovery by up to 40%, impairs glucose regulation, and elevates cortisol. Prioritise 7–9h tonight.",
                severity = InsightSeverity.CRITICAL,
                emoji = "🛌"
            )
        } else if (todaySleepHours < sleepGoal - 1f && todaySleepHours > 0f) {
            insights += InsightCard(
                title = "Below Sleep Target",
                body = "You logged ${String.format("%.1f", todaySleepHours)}h vs your ${sleepGoal.toInt()}h goal. You're ${String.format("%.1f", sleepGoal - todaySleepHours)}h short. Aim for an earlier bedtime tonight to protect recovery.",
                severity = InsightSeverity.WARNING,
                emoji = "🌙"
            )
        } else if (todaySleepHours >= sleepGoal) {
            insights += InsightCard(
                title = "Sleep Goal Achieved",
                body = "Excellent! ${String.format("%.1f", todaySleepHours)}h of sleep logged. Your body is in an optimal recovery state today — great day for higher-intensity training.",
                severity = InsightSeverity.POSITIVE,
                emoji = "⭐"
            )
        }

        // Protein recovery gap
        val proteinTarget = goals.dailyProteinGoal
        val proteinRatio = if (proteinTarget > 0f) todayProtein / proteinTarget else 0f
        val weekProteinAvg = if (weekFood.isNotEmpty()) weekFood.sumOf { it.proteinGrams.toDouble() }.toFloat() / 7f else 0f
        if (proteinRatio < 0.5f && todayCalories > 0) {
            val needed = (proteinTarget - todayProtein).toInt()
            insights += InsightCard(
                title = "Protein Recovery Gap",
                body = "You're at ${todayProtein.toInt()}g protein (${(proteinRatio * 100).toInt()}% of your ${proteinTarget.toInt()}g goal). Without sufficient protein, muscle repair slows significantly. Add ${needed}g more — try 150g chicken (45g), 200g Greek yogurt (17g), or 3 eggs (18g).",
                severity = InsightSeverity.CRITICAL,
                emoji = "💪"
            )
        } else if (proteinRatio >= 1f) {
            insights += InsightCard(
                title = "Protein Target Crushed",
                body = "You've hit ${todayProtein.toInt()}g protein today (${(proteinRatio * 100).toInt()}% of goal). Muscle protein synthesis is optimally fuelled. Keep it consistent throughout the week for best results.",
                severity = InsightSeverity.POSITIVE,
                emoji = "🏆"
            )
        }

        // Hydration analysis
        val waterGoalMl = goals.dailyWaterGoal
        val waterRatio = if (waterGoalMl > 0) todayWaterMl.toFloat() / waterGoalMl else 0f
        val hydrationStreak = (6 downTo 0).count { daysAgo ->
            (dailyData.getOrNull(6 - daysAgo)?.waterMl ?: 0) >= waterGoalMl * 0.9f
        }
        if (waterRatio < 0.4f && todayCalories > 0) {
            insights += InsightCard(
                title = "Dehydration Risk",
                body = "You've only had ${todayWaterMl}ml of water today (${(waterRatio * 100).toInt()}% of your ${waterGoalMl}ml goal). Even mild dehydration (1–2%) reduces cognitive performance by 20% and impairs fat metabolism. Drink 500ml now.",
                severity = InsightSeverity.CRITICAL,
                emoji = "💧"
            )
        } else if (hydrationStreak >= 3) {
            insights += InsightCard(
                title = "Hydration Streak 🔥 ($hydrationStreak days)",
                body = "You've hit your hydration goal for $hydrationStreak consecutive days. Consistent hydration supports better skin health, energy levels, and toxin elimination. Keep it up!",
                severity = InsightSeverity.POSITIVE,
                emoji = "🌊"
            )
        } else if (waterRatio < 0.7f && waterRatio > 0f) {
            val remaining = waterGoalMl - todayWaterMl
            insights += InsightCard(
                title = "Hydration Top-Up Needed",
                body = "${todayWaterMl}ml logged so far. Drink ${remaining}ml more to reach your daily goal. A good strategy: keep a 500ml bottle at your desk and finish it before each meal.",
                severity = InsightSeverity.WARNING,
                emoji = "🥤"
            )
        }

        // Calorie surplus/deficit analysis
        val calGoal = goals.dailyCalorieGoal
        val calRatio = if (calGoal > 0) todayCalories.toFloat() / calGoal else 0f
        if (calRatio > 1.2f) {
            val surplus = todayCalories - calGoal
            insights += InsightCard(
                title = "Calorie Surplus Alert",
                body = "You've consumed ${todayCalories}kcal — ${surplus}kcal over your ${calGoal}kcal target. Consistent surpluses lead to fat storage. Consider a lighter dinner and skip dessert or high-calorie snacks tonight.",
                severity = InsightSeverity.WARNING,
                emoji = "🔥"
            )
        } else if (calRatio in 0.85f..1.1f) {
            insights += InsightCard(
                title = "Calorie Balance On Point",
                body = "You're at ${todayCalories}kcal (${(calRatio * 100).toInt()}% of your ${calGoal}kcal target). Excellent macro management — your body has the right fuel for performance today.",
                severity = InsightSeverity.POSITIVE,
                emoji = "✅"
            )
        }

        // Fiber analysis
        if (todayFiber < goals.dailyFiberGoal * 0.5f && todayCalories > 0) {
            insights += InsightCard(
                title = "Fiber Intake Low",
                body = "You've only logged ${todayFiber.toInt()}g fiber vs your ${goals.dailyFiberGoal.toInt()}g goal. Low fiber is linked to poor gut health and slower satiety. Add leafy greens, legumes, or oats to your next meal.",
                severity = InsightSeverity.WARNING,
                emoji = "🥗"
            )
        }

        // 7-day nutrition trend
        val activeFoodDays = dailyData.count { it.calories > 100 }
        if (activeFoodDays >= 4) {
            val avgWeekCalories = dailyData.filter { it.calories > 0 }.map { it.calories }.average().toInt()
            val avgWeekProtein = dailyData.filter { it.protein > 0f }.map { it.protein }.average().toFloat()
            insights += InsightCard(
                title = "7-Day Nutrition Trend",
                body = "Over the past week you've averaged ${avgWeekCalories}kcal/day and ${avgWeekProtein.toInt()}g protein/day (goal: ${calGoal}kcal, ${proteinTarget.toInt()}g). ${if (avgWeekProtein >= proteinTarget * 0.8f) "Strong protein consistency!" else "Focus on closing the protein gap this week."}",
                severity = InsightSeverity.INFO,
                emoji = "📈"
            )
        }

        // No data onboarding insight
        if (!hasData) {
            insights += InsightCard(
                title = "Start Your Health Journey",
                body = "No data logged yet today. Log your first meal, water intake, or sleep session to unlock your personalised AI health score and insights.",
                severity = InsightSeverity.INFO,
                emoji = "🚀"
            )
        }

        // Sort: CRITICAL first, then WARNING, POSITIVE, INFO
        val sortedInsights = insights.sortedBy {
            when (it.severity) {
                InsightSeverity.CRITICAL -> 0
                InsightSeverity.WARNING -> 1
                InsightSeverity.POSITIVE -> 2
                InsightSeverity.INFO -> 3
            }
        }

        // ── Today's Action Plan (top 3 priorities) ───────────────────────────
        val actions = mutableListOf<String>()
        if (waterRatio < 0.8f && waterGoalMl > 0) {
            val remaining = waterGoalMl - todayWaterMl
            actions += "Drink ${remaining.coerceAtLeast(200)}ml of water before your next meal"
        }
        if (proteinRatio < 0.8f && proteinTarget > 0f) {
            val needed = (proteinTarget - todayProtein).toInt().coerceAtLeast(1)
            actions += "Add ${needed}g protein to your diet today (e.g. Greek yogurt, eggs, or chicken)"
        }
        if (todaySleepHours < sleepGoal - 0.5f && todaySleepHours > 0f) {
            actions += "Plan an early bedtime tonight to recover sleep deficit (target: ${sleepGoal.toInt()}h)"
        } else if (todaySleepHours == 0f) {
            actions += "Log your sleep tonight to track recovery and unlock sleep insights"
        }
        if (calRatio > 1.15f) {
            actions += "Keep dinner light — you're ${(todayCalories - calGoal).coerceAtLeast(50)}kcal over your daily target"
        }
        if (todayFiber < goals.dailyFiberGoal * 0.5f && todayCalories > 0) {
            actions += "Add a high-fiber food to your next meal: spinach salad, oats, lentils, or chia seeds"
        }
        if (actions.isEmpty()) {
            actions += "Keep up the great work — all core metrics are on track today! 🎉"
        }

        // ── Weekly Summary ───────────────────────────────────────────────────
        val avgCaloriesWeek = dailyData.filter { it.calories > 0 }.map { it.calories }.average().takeIf { !it.isNaN() }?.toInt() ?: 0
        val avgSleepWeek = dailyData.filter { it.sleepHours > 0f }.map { it.sleepHours }.average().takeIf { !it.isNaN() }?.toFloat() ?: 0f
        val avgWaterWeek = dailyData.filter { it.waterMl > 0 }.map { it.waterMl }.average().takeIf { !it.isNaN() }?.toInt() ?: 0

        val weeklySummary = buildWeeklySummary(
            avgCalories = avgCaloriesWeek,
            avgSleep = avgSleepWeek,
            avgWaterMl = avgWaterWeek,
            goals = goals,
            overallScore = overallScore,
            activeDays = activeFoodDays
        )

        return AiCoachReport(
            overallScore = overallScore.coerceIn(0, 100),
            nutritionScore = nutritionScore.coerceIn(0, 100),
            hydrationScore = hydrationScore.coerceIn(0, 100),
            sleepScore = sleepScore.coerceIn(0, 100),
            activityScore = activityScore.coerceIn(0, 100),
            scoreDelta = scoreDelta,
            scoreLabel = scoreLabel(overallScore),
            insightCards = sortedInsights.take(8),
            actionPlan = actions.take(3),
            weeklySummary = weeklySummary,
            hasData = hasData
        )
    }

    // ── Score helpers ─────────────────────────────────────────────────────────

    private fun computeNutritionScore(calories: Int, protein: Float, fiber: Float, goals: UserGoals): Int {
        if (calories == 0 && protein == 0f) return 0
        val calGoal = goals.dailyCalorieGoal
        val calScore = if (calGoal > 0) {
            val ratio = calories.toFloat() / calGoal
            when {
                ratio in 0.85f..1.05f -> 100
                ratio in 0.70f..0.85f -> 80
                ratio in 1.05f..1.20f -> 80
                ratio in 0.50f..0.70f -> 55
                ratio > 1.20f -> 55
                else -> 25
            }
        } else 50

        val protScore = if (goals.dailyProteinGoal > 0f) {
            val ratio = protein / goals.dailyProteinGoal
            (ratio * 100).toInt().coerceIn(0, 100)
        } else 50

        val fiberScore = if (goals.dailyFiberGoal > 0f) {
            val ratio = fiber / goals.dailyFiberGoal
            (ratio * 100).toInt().coerceIn(0, 100)
        } else 50

        return ((calScore * 0.4 + protScore * 0.4 + fiberScore * 0.2).toInt()).coerceIn(0, 100)
    }

    private fun computeHydrationScore(waterMl: Int, goalMl: Int): Int {
        if (goalMl <= 0) return 50
        if (waterMl <= 0) return 0
        val ratio = waterMl.toFloat() / goalMl
        return (ratio * 100).toInt().coerceIn(0, 100)
    }

    private fun computeSleepScore(sleepHours: Float, quality: Int, goalHours: Float): Int {
        if (sleepHours <= 0f) return 0
        val goal = goalHours.takeIf { it > 0f } ?: 8f
        val durationScore = (sleepHours / goal * 80f).toInt().coerceIn(0, 80)
        val qualityScore = ((quality.toFloat() / 5f) * 20f).toInt().coerceIn(0, 20)
        return (durationScore + qualityScore).coerceIn(0, 100)
    }

    private fun scoreLabel(score: Int): String = when {
        score >= 85 -> "Excellent"
        score >= 70 -> "Good"
        score >= 50 -> "Fair"
        score >= 30 -> "Low"
        else -> "Poor"
    }

    private fun buildWeeklySummary(
        avgCalories: Int,
        avgSleep: Float,
        avgWaterMl: Int,
        goals: UserGoals,
        overallScore: Int,
        activeDays: Int
    ): String {
        if (activeDays < 2) {
            return "Not enough data for a weekly summary yet. Log at least 3 days of meals, water, and sleep to see your personalised 7-day health analysis here."
        }

        val calGoal = goals.dailyCalorieGoal
        val sleepGoal = goals.dailySleepGoalHours
        val waterGoal = goals.dailyWaterGoal

        val calStatus = if (calGoal > 0 && avgCalories > 0) {
            val diff = ((avgCalories - calGoal) * 100f / calGoal).toInt()
            when {
                diff in -10..10 -> "calorie intake was well-balanced (avg ${avgCalories}kcal/day)"
                diff > 10 -> "calorie intake was ${diff}% above your goal (avg ${avgCalories}kcal/day) — consider monitoring portion sizes"
                else -> "calorie intake was ${-diff}% below your goal (avg ${avgCalories}kcal/day) — ensure you're eating enough to fuel recovery"
            }
        } else "insufficient calorie data logged this week"

        val sleepStatus = when {
            avgSleep <= 0f -> "no sleep data logged"
            avgSleep >= sleepGoal -> "sleep averaged ${String.format("%.1f", avgSleep)}h/night — meeting your recovery target"
            avgSleep >= sleepGoal - 1f -> "sleep averaged ${String.format("%.1f", avgSleep)}h/night — slightly below your ${sleepGoal.toInt()}h goal"
            else -> "sleep averaged only ${String.format("%.1f", avgSleep)}h/night — significantly below the recommended ${sleepGoal.toInt()}h"
        }

        val waterStatus = when {
            avgWaterMl <= 0 -> "no hydration data logged"
            avgWaterMl >= waterGoal * 0.9f -> "hydration was consistent (avg ${avgWaterMl}ml/day)"
            else -> "hydration averaged ${avgWaterMl}ml/day — below your ${waterGoal}ml goal"
        }

        val focus = when {
            avgSleep < sleepGoal - 1f -> "This week, prioritise earlier bedtimes to close your sleep deficit — recovery quality directly impacts metabolism and muscle repair."
            avgWaterMl < waterGoal * 0.7f -> "Focus on consistent daily hydration next week — try keeping a water bottle visible at all times."
            overallScore < 60 -> "Overall wellness has room to improve. Start with one small habit: log every meal and drink a full glass of water before each one."
            else -> "You're building strong health habits. Keep the momentum going and aim for 5+ consistent days next week."
        }

        return "This week, your $calStatus. Your $sleepStatus. Your $waterStatus. $focus"
    }

    // ── Internal data class for per-day stats ─────────────────────────────────
    private data class DayData(
        val dayStart: Long,
        val calories: Int,
        val protein: Float,
        val waterMl: Int,
        val sleepHours: Float,
        val sleepQuality: Int
    )
}
