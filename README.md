# Nourish Fit Track — Nutrition, Sleep, AI, You

A modern, high-performance Android health, nutrition, sleep, and wellness tracking application built with Jetpack Compose, Material 3, Kotlin Coroutines & Flow, and Room Database. Nourish Fit Track helps users log meals, search live OpenFoodFacts food databases, track macronutrients & micronutrients, monitor sleep & hydration, run hardware step counting, sync with Google Health Connect, and receive on-device AI health intelligence insights.

---

## ⚡ Key Features

- 🔍 **Live OpenFoodFacts REST API Search & Barcode Lookup**: Instant online product search and barcode lookup for over 3M+ packaged foods alongside a 300+ pre-populated regional database (Kerala, Tamil Nadu, Karnataka, South & North Indian dishes).
- 🤖 **On-Device AI Health Intelligence Engine**: 0-100 Daily Wellness Score, severity-coded health insights, interactive action plan checklist, and weekly health summary running 100% locally on-device.
- 🔗 **Google Health Connect SDK Integration**: Bi-directional sync with Google Health Connect (`androidx.health.connect:connect-client:1.1.0-alpha10`) for Nutrition, Hydration, and Sleep data.
- 🚶 **Background Hardware Step Tracker**: Persistent foreground step counting service (`StepTrackingService`) using `Sensor.TYPE_STEP_COUNTER` with auto-start on device boot (`BootCompletedReceiver`).
- 📊 **Trends & Interactive Analytics**: Calorie intake, hydration level, sleep stage breakdown, step progress, and macronutrient ratio charts for 7-day, 30-day, and 1-year history.
- 💾 **Data Export & Privacy (JSON / CSV)**: Export complete food, water, and sleep logs to structured JSON or CSV files directly from Settings. 100% local Room DB privacy with zero external ads or paid paywalls.

---

## 🛠️ Tech Stack & Dependencies

- **Language**: Kotlin `2.2.10`
- **Android SDK**: `compileSdk 35` | `targetSdk 35` | `minSdk 26`
- **JDK**: Java 21 (`JavaVersion.VERSION_21`)
- **Android Gradle Plugin**: `9.3.1`
- **UI Framework**: Jetpack Compose BOM (`2024.02.00`) + Material 3
- **Navigation**: Navigation Compose (`2.7.7`)
- **Database & Persistence**: Room `2.6.1` with KSP (`2.3.6`)
- **Health Data SDK**: Google Health Connect Client (`1.1.0-alpha10`)
- **Async Processing**: Kotlin Coroutines (`1.7.3`) & `StateFlow`
- **Testing**: JUnit 4 (`4.13.2`), `kotlinx-coroutines-test`, Room Testing (`2.6.1`)

---

## 🏗️ Architecture & Code Structure

Nourish Fit Track follows modern Android **MVVM (Model-View-ViewModel)** architecture paired with the **Repository Pattern**:

- **Database Layer**: [FitnessDatabase.kt](file:///c:/Users/aniru/Music/Fitness/app/src/main/java/com/fitnessapp/data/db/FitnessDatabase.kt) exposes Room DAOs for reactive data access.
- **Repository Layer**: Encapsulates data fetching and Room interactions with `CoroutineDispatcher = Dispatchers.IO` dependency injection.
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
├── service
│   ├── StepTrackingService.kt (Hardware step counter foreground service)
│   └── BootCompletedReceiver.kt (Auto-starts step service on boot)
├── ui
│   ├── components (AppCard, AppProgressBar, FrostedGlass, RingProgress, MacroDonutChart, WeeklyBarChart)
│   ├── navigation (NavGraph.kt - 4-tab Navigation Compose graph)
│   ├── screens
│   │   ├── home (HomeScreen.kt, HomeViewModel.kt)
│   │   ├── food (FoodLogScreen.kt, AddFoodScreen.kt, NutritionDetailsScreen.kt, FoodViewModel.kt)
│   │   ├── sleep (SleepLogScreen.kt, AddSleepScreen.kt, SleepViewModel.kt)
│   │   ├── ai (AiScreen.kt, AiViewModel.kt)
│   │   ├── analytics (AnalyticsScreen.kt, AnalyticsViewModel.kt)
│   │   └── settings (SettingsScreen.kt, SettingsViewModel.kt)
│   └── theme (Color.kt, Theme.kt, Type.kt)
└── util
    ├── BarcodeScannerUtil.kt (OpenFoodFacts REST API & EAN/UPC parser)
    ├── HealthConnectManager.kt (Google Health Connect SDK helper)
    ├── HealthIntelligenceEngine.kt (On-device wellness engine)
    └── DateUtils.kt (Date & epoch calculation utilities)
```

---

## 📱 Navigation & Core Screens

| Tab / Screen | Route | Description |
| :--- | :--- | :--- |
| **Overview (Home)** | `overview` ([HomeScreen.kt](file:///c:/Users/aniru/Music/Fitness/app/src/main/java/com/fitnessapp/ui/screens/home/HomeScreen.kt)) | Main dashboard with calorie ring dial, streak badge, quick action chips (`+ Meal`, `+ Sleep`, `Analytics`, `AI Hub`), hydration logger, and step progress. |
| **Nutrition Tab** | `nutrition` ([FoodLogScreen.kt](file:///c:/Users/aniru/Music/Fitness/app/src/main/java/com/fitnessapp/ui/screens/food/FoodLogScreen.kt)) | Meal log breakdown (Breakfast, Lunch, Dinner, Snacks) for today, week, and month with swipe-to-delete and nutrition detail links. |
| **Add / Edit Food** | `add_food` ([AddFoodScreen.kt](file:///c:/Users/aniru/Music/Fitness/app/src/main/java/com/fitnessapp/ui/screens/food/AddFoodScreen.kt)) | Top search bar querying local database AND live OpenFoodFacts REST API / barcode lookup with serving size adjusters. |
| **Sleep Log** | `sleep` ([SleepLogScreen.kt](file:///c:/Users/aniru/Music/Fitness/app/src/main/java/com/fitnessapp/ui/screens/sleep/SleepLogScreen.kt)) | Sleep score calculator (0–100), sleep duration vs daily target, estimated REM/Deep/Light stages, and weekly sleep history chart. |
| **AI Health Hub** | `ai` ([AiScreen.kt](file:///c:/Users/aniru/Music/Fitness/app/src/main/java/com/fitnessapp/ui/screens/ai/AiScreen.kt)) | 0–100 Wellness score, prompt bar, personalized severity-coded insights, interactive action plan checklist, and 7-day digest. |
| **Analytics & Trends** | `analytics` ([AnalyticsScreen.kt](file:///c:/Users/aniru/Music/Fitness/app/src/main/java/com/fitnessapp/ui/screens/analytics/AnalyticsScreen.kt)) | Detailed historical trends for Calories, Water, Sleep, Steps, and Macro ratios across 7-day, 30-day, and 1-year timeframes. |
| **Settings & Backup** | `more` ([SettingsScreen.kt](file:///c:/Users/aniru/Music/Fitness/app/src/main/java/com/fitnessapp/ui/screens/settings/SettingsScreen.kt)) | Goal adjusters, Google Health Connect launcher, hardware step sensor control, JSON/CSV health data export, and clear data controls. |

---

## 🎨 Design System & Color Tokens

Defined in [Color.kt](file:///c:/Users/aniru/Music/Fitness/app/src/main/java/com/fitnessapp/ui/theme/Color.kt):

- `BackgroundDark` (`#0B0E14`): Space dark background surface.
- `SurfaceCard` (`#161922`): Elevating card container surface.
- `SurfaceCardAlt` (`#1E2433`): Elevated tile and input field background.
- `AccentGreen` (`#00E676`): Primary emerald accent for protein, step progress, and positive metrics.
- `AccentOrange` (`#FF6D00`): Calorie intake accent and warning highlights.
- `AccentBlue` (`#00B0FF`): Hydration, sleep metrics, and OpenFoodFacts API highlights.
- `AccentPurple` (`#7B61FF`): Soft indigo accent for sleep stages and fiber metrics.

---

## 🧪 Building & Running

### Prerequisites
- JDK 21 configured on `JAVA_HOME`.
- Android Studio Ladybug / Jellyfish or newer.

### Build Commands (Windows / PowerShell)

```powershell
# Run Full Unit Test Suite
.\gradlew.bat test

# Assemble Debug APK
.\gradlew.bat assembleDebug
```

---

## 🔒 Privacy & Open Source Guarantee

- **100% Local Storage**: All health entries are saved directly to your device's private Room database (`fitness_database.db`).
- **No Ads, Paywalls, or Tracking**: Zero third-party trackers, zero billing dependencies, zero subscription locks.
