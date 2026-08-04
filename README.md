# Nourish — Nutrition, Sleep, You

<!-- IMPORTANT: Maintainer Rule -->
<!-- Whenever a screen, Room entity, DAO, or major component is added, updated, or removed, update this README.md in the same commit to prevent documentation drift. -->

A modern, high-performance Android health and wellness tracking application built with Jetpack Compose, Material 3, Kotlin Coroutines & Flow, and Room Database. Nourish helps users log meals, track macronutrients & micronutrients, monitor sleep stages, track hydration and daily step goals, and analyze health trends over time.

---

## Tech Stack & Versions

- **Language**: Kotlin `2.2.10`
- **Android SDK**: `compileSdk 34` | `targetSdk 34` | `minSdk 26`
- **JDK**: Java 17 (`JavaVersion.VERSION_17`)
- **Android Gradle Plugin (AGP)**: `9.3.1`
- **Jetpack Compose**: Compose BOM `2024.02.00` with Material 3
- **Navigation**: Navigation Compose `2.7.7`
- **Database & Persistence**: Room `2.6.1` with KSP (`2.3.6`)
- **Async & Reactive Flow**: Kotlin Coroutines `1.7.3` & `StateFlow`
- **Lifecycle Runtime**: AndroidX Lifecycle `2.7.0`
- **Testing**: JUnit 4 (`4.13.2`), `kotlinx-coroutines-test`, Room Testing (`2.6.1`)

---

## Architecture & Code Structure

Nourish follows modern Android **MVVM (Model-View-ViewModel)** architecture paired with the **Repository Pattern**:

- **Database Layer**: [FitnessDatabase.kt](file:///c:/Users/aniru/Music/Fitness/app/src/main/java/com/fitnessapp/data/db/FitnessDatabase.kt) exposes Room DAOs for reactive data access.
- **Repository Layer**: Encapsulates data fetching and Room interactions with `CoroutineDispatcher = Dispatchers.IO` dependency injection for deterministic unit testing.
- **UI Layer (Jetpack Compose)**: Screens consume reactive `StateFlow` state exposed by ViewModels, rendering glassmorphic Material 3 interfaces.

```text
com.fitnessapp
├── data
│   ├── db
│   │   ├── FitnessDatabase.kt
│   │   ├── dao (FoodEntryDao, SleepEntryDao, WaterEntryDao, StepsEntryDao, UserGoalsDao)
│   │   └── entity (FoodEntry, SleepEntry, WaterEntry, StepsEntry, UserGoals)
│   ├── repository (FoodRepository, SleepRepository, WaterRepository, StepsRepository, SettingsRepository)
│   └── FoodDatabase.kt (Pre-populated database of 300+ foods and regional dishes)
├── ui
│   ├── components
│   │   ├── AppCard.kt
│   │   ├── AppProgressBar.kt
│   │   ├── DateSelector.kt
│   │   ├── FrostedGlass.kt
│   │   ├── InsightCard.kt
│   │   ├── LinearBar.kt
│   │   ├── RingProgress.kt
│   │   └── charts (MacroDonutChart.kt, WeeklyBarChart.kt, LineChart.kt, Sparkline.kt)
│   ├── navigation (NavGraph.kt - 5-tab Navigation Compose graph)
│   ├── screens
│   │   ├── home (HomeScreen.kt, HomeViewModel.kt)
│   │   ├── food (FoodLogScreen.kt, AddFoodScreen.kt, NutritionDetailsScreen.kt, FoodViewModel.kt)
│   │   ├── sleep (SleepLogScreen.kt, AddSleepScreen.kt, SleepViewModel.kt)
│   │   ├── analytics (AnalyticsScreen.kt, AnalyticsViewModel.kt)
│   │   └── settings (SettingsScreen.kt, SettingsViewModel.kt)
│   └── theme (Color.kt, Theme.kt, Type.kt)
└── util (DateUtils.kt)
```

---

## Screens & Features

| Screen | Route / File Path | Description |
| :--- | :--- | :--- |
| **Overview (Home)** | `overview` ([HomeScreen.kt](file:///c:/Users/aniru/Music/Fitness/app/src/main/java/com/fitnessapp/ui/screens/home/HomeScreen.kt)) | Daily dashboard featuring Calorie Ring Progress, Macro progress bars, Quick Water (+250ml) & Steps (+1000 steps) buttons, Sleep quality summary, and dynamic AI Insight card. Bottom nav clicks to Overview navigate smoothly without backstack conflicts. |
| **Nutrition Log** | `nutrition` ([FoodLogScreen.kt](file:///c:/Users/aniru/Music/Fitness/app/src/main/java/com/fitnessapp/ui/screens/food/FoodLogScreen.kt)) | Meal log breakdown (Breakfast, Lunch, Dinner, Snacks) for today, week, and month. Supports swipe-to-delete with undo snackbar and link to details. |
| **Add / Edit Food** | `add_food?id={id}` ([AddFoodScreen.kt](file:///c:/Users/aniru/Music/Fitness/app/src/main/java/com/fitnessapp/ui/screens/food/AddFoodScreen.kt)) | Search & select from 300+ pre-populated foods (including regional dishes) with serving multipliers, or switch to Custom Meal mode to log exact custom macros/micros. |
| **Nutrition Details** | `nutrition_details` ([NutritionDetailsScreen.kt](file:///c:/Users/aniru/Music/Fitness/app/src/main/java/com/fitnessapp/ui/screens/food/NutritionDetailsScreen.kt)) | Micronutrient breakdown screen tracking daily Fiber, Sugar, Sodium, and Cholesterol levels against daily recommended targets. |
| **Sleep Log** | `sleep` ([SleepLogScreen.kt](file:///c:/Users/aniru/Music/Fitness/app/src/main/java/com/fitnessapp/ui/screens/sleep/SleepLogScreen.kt)) | Sleep tracking overview displaying weighted Sleep Score (0–100), sleep duration vs target, estimated stage breakdown (REM, Deep, Light, Awake), and an integrated **Hydration Tracker Card** (+250ml / +500ml quick-add buttons). |
| **Add / Edit Sleep** | `add_sleep?id={id}` ([AddSleepScreen.kt](file:///c:/Users/aniru/Music/Fitness/app/src/main/java/com/fitnessapp/ui/screens/sleep/AddSleepScreen.kt)) | Form to log bedtime, wake time, quality rating (1–5 stars), and sleep notes. Persists start & wake timestamps accurately to `sleep_entries`. |
| **Analytics** | `analytics` ([AnalyticsScreen.kt](file:///c:/Users/aniru/Music/Fitness/app/src/main/java/com/fitnessapp/ui/screens/analytics/AnalyticsScreen.kt)) | Health trends for Week (7d), Month (30d), and Year (365d) calculated strictly from real user data. Displays clean zero-state metrics (0 kcal, 0 L water, empty charts) when no logs exist, with zero artificial demo fallbacks. |
| **More (Settings)** | `more` ([SettingsScreen.kt](file:///c:/Users/aniru/Music/Fitness/app/src/main/java/com/fitnessapp/ui/screens/settings/SettingsScreen.kt)) | 4-section screen: (1) Profile Row, (2) 4 Quick Shortcuts Grid (Goals, Reminders, My Data, Health Connect), (3) Daily Goals Form, and (4) Help & About list. |

---

## Data Model (Room Entities)

### 1. `food_entries` ([FoodEntry.kt](file:///c:/Users/aniru/Music/Fitness/app/src/main/java/com/fitnessapp/data/db/entity/FoodEntry.kt))

| Field Name | Type | Notes |
| :--- | :--- | :--- |
| `id` | `Long` | Primary Key (auto-generated) |
| `name` | `String` | Food / dish name |
| `calories` | `Int` | Calorie count (kcal) |
| `proteinGrams` | `Float` | Protein in grams |
| `carbsGrams` | `Float` | Carbohydrates in grams |
| `fatGrams` | `Float` | Total fats in grams |
| `fiberGrams` | `Float` | Dietary fiber in grams |
| `sugarGrams` | `Float` | Total sugar in grams |
| `sodiumMg` | `Float` | Sodium in milligrams |
| `cholesterolMg` | `Float` | Cholesterol in milligrams |
| `mealType` | `String` | Meal category ("Breakfast", "Lunch", "Dinner", "Snack") |
| `dateMillis` | `Long` | Epoch timestamp in milliseconds |

### 2. `sleep_entries` ([SleepEntry.kt](file:///c:/Users/aniru/Music/Fitness/app/src/main/java/com/fitnessapp/data/db/entity/SleepEntry.kt))

| Field Name | Type | Notes |
| :--- | :--- | :--- |
| `id` | `Long` | Primary Key (auto-generated) |
| `startMillis` | `Long` | Bedtime timestamp in milliseconds |
| `endMillis` | `Long` | Wake time timestamp in milliseconds |
| `quality` | `Int` | Quality score rating (1–5 star rating) |
| `notes` | `String` | Optional sleep notes |
| `dateMillis` | `Long` | Epoch timestamp in milliseconds |

### 3. `water_entries` ([WaterEntry.kt](file:///c:/Users/aniru/Music/Fitness/app/src/main/java/com/fitnessapp/data/db/entity/WaterEntry.kt))

| Field Name | Type | Notes |
| :--- | :--- | :--- |
| `id` | `Long` | Primary Key (auto-generated) |
| `amountMl` | `Int` | Water volume in milliliters |
| `dateMillis` | `Long` | Epoch timestamp in milliseconds |

### 4. `steps_entries` ([StepsEntry.kt](file:///c:/Users/aniru/Music/Fitness/app/src/main/java/com/fitnessapp/data/db/entity/StepsEntry.kt))

| Field Name | Type | Notes |
| :--- | :--- | :--- |
| `id` | `Long` | Primary Key (auto-generated) |
| `count` | `Int` | Step count for the day |
| `dateMillis` | `Long` | Epoch timestamp in milliseconds |

### 5. `user_goals` ([UserGoals.kt](file:///c:/Users/aniru/Music/Fitness/app/src/main/java/com/fitnessapp/data/db/entity/UserGoals.kt))

| Field Name | Type | Notes |
| :--- | :--- | :--- |
| `id` | `Int` | Primary Key (Fixed `1` for single user profile) |
| `dailyCalorieGoal` | `Int` | Target daily calories (default `2200`) |
| `dailyProteinGoal` | `Float` | Target daily protein in grams (default `140f`) |
| `dailyCarbsGoal` | `Float` | Target daily carbs in grams (default `250f`) |
| `dailyFatGoal` | `Float` | Target daily fat in grams (default `70f`) |
| `dailyFiberGoal` | `Float` | Target daily fiber in grams (default `30f`) |
| `dailyWaterGoal` | `Int` | Target daily water in ml (default `2500`) |
| `dailySleepGoalHours` | `Float` | Target daily sleep in hours (default `8.0f`) |
| `dailyStepsGoal` | `Int` | Target daily steps (default `10000`) |

---

## Sleep Scoring & Stage Estimation Algorithm

### 1. Weighted Sleep Score Algorithm (0–100)

Implemented in [SleepViewModel.kt](file:///c:/Users/aniru/Music/Fitness/app/src/main/java/com/fitnessapp/ui/screens/sleep/SleepViewModel.kt) to compute continuous, realistic scores (e.g. 82, 89, 74):

- **Duration Adequacy (40% Weight / 40 pts max)**: Evaluates logged sleep duration against `userGoals.dailySleepGoalHours`. Perfect at 100% of goal. Oversleeping past 1.25x target incurs a diminishing returns penalty.
- **Bedtime Consistency (30% Weight / 30 pts max)**: Evaluates bedtime regularity against the user's 7-day historical mean bedtime. Deviations under 15 minutes achieve 30 pts.
- **Self-Reported Quality (30% Weight / 30 pts max)**: Normalizes the 1–5 star rating into a 6–30 point scale.

### 2. Honest Stage Estimation (Option A)

Since manual logging cannot measure hardware EEG/wearable sleep stages, the Sleep Stages Card includes an explicit badge (`"Estimated — duration & quality"`). Stage proportions (Awake, REM, Light, Deep) dynamically shift based on total sleep duration and sleep score (e.g., lower-quality rest yields higher Awake % and reduced Deep % while maintaining a 100% stage sum).

---

## Design System & Color Tokens

Color tokens defined in [Color.kt](file:///c:/Users/aniru/Music/Fitness/app/src/main/java/com/fitnessapp/ui/theme/Color.kt):

| Token Name | Hex Code | Purpose |
| :--- | :--- | :--- |
| `BackgroundDark` | `#0A0A0E` | Main dark background |
| `SurfaceCard` | `#16161C` | AppCard background surface |
| `SurfaceCardAlt` | `#1E1E26` | Elevated tile & field background |
| `BorderSubtle` | `#14FFFFFF` | White @ 8% alpha border |
| `TextPrimary` | `#F5F5F7` | Primary headline and body text |
| `TextSecondary` | `#9B9BA8` | Subtitles and input labels |
| `TextTertiary` | `#6B6B76` | Muted captions and chevron icons |
| `AccentOrange` | `#E8963C` | Calorie metrics & calorie charts |
| `AccentGreen` | `#4CD97B` | Protein, steps, active tab, buttons |
| `AccentBlue` | `#4A9EE8` | Water, sleep, carbs metrics |
| `AccentPurple` | `#8B7CF6` | Fiber metric & secondary indicators |
| `AccentYellow` | `#E8C23C` | Fats metric |
| `AccentRed` | `#E85C5C` | Sodium / Cholesterol warnings & delete buttons |
| `BrandGradientStart` | `#E8963C` | Warm Orange gradient accent start |
| `BrandGradientEnd` | `#4A9EE8` | Cool Blue gradient accent end |

### Frosted Glassmorphism

Implemented in [FrostedGlass.kt](file:///c:/Users/aniru/Music/Fitness/app/src/main/java/com/fitnessapp/ui/components/FrostedGlass.kt) via custom `Modifier.frostedGlass(...)`:

- **API 31+ (Android 12+)**: Rendered via hardware `RenderEffect.createBlurEffect`.
- **API < 31 Fallback**: Renders an opaque fallback background (`SurfaceCard` / `BackgroundDark`) to preserve compatibility on older devices.
- **Usage**: Applied to Top App Bars, Bottom Navigation Bar, and Pinned Bottom Buttons.

---

## Known Limitations & Stubs

- **Shortcuts Grid Stubs**: The *Reminders*, *My Data*, and *Health Connect* shortcut tiles in the More tab display informative snackbars when clicked. Direct hardware Health Connect and push notifications are reserved for future releases.
- **Step Tracking**: Step counts can be logged and incremented manually via quick-add buttons on the Overview tab; background hardware accelerometer step-counter sensors are not bound yet.
- **Barcode Scanner**: The barcode scanner icon in Add Food displays a notification stub.

---

## Setup & Running Instructions

### 1. Prerequisites

- **JDK**: Java 17 installed and configured on your `JAVA_HOME`.
- **Android Studio**: Android Studio Jellyfish / Ladybug or newer.

### 2. Command Line Build Commands (Windows / PowerShell)

```powershell
# Assemble Debug APK
.\gradlew.bat assembleDebug

# Run Full Unit Test Suite
.\gradlew.bat test
```

### 3. Emulator & Device Requirements

- Supported on Android devices/emulators running **Android 8.0 (API 26)** or higher.
- To view full hardware blur frosted glassmorphism effects, run on **Android 12 (API 31)** or higher.

---

## Contributing Note

> **Maintainer Rule**: Whenever a screen, entity, or major shared component is added, modified, or deleted in this codebase, update this `README.md` file within the same commit.
