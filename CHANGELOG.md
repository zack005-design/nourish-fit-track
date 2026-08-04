# Changelog

## [1.2.5] - 2026-08-04

- **Health Connect API Sync**: Added availability detection and record validation helpers to `HealthConnectManager.kt` along with comprehensive unit testing (`HealthConnectManagerTest.kt`).

## [1.2.4] - 2026-08-04

- **Google Health Connect Manager & Launcher**: Created `HealthConnectManager.kt` supporting multi-intent fallback (`android.health.connect.action.HEALTH_CONNECT_SETTINGS`, `androidx.health.ACTION_HEALTH_CONNECT_SETTINGS`, and Play Store fallback). Added modal dialog in `SettingsScreen.kt` supporting Google Health data read, write sync, and settings launch.
- **Floating Dark Glass Snackbar UI**: Styled `SnackbarHost` in `NavGraph.kt` with bottom padding (`72.dp` above navigation bar) and a dark glass shape (`#1E2433`, rounded `16.dp` corners), preventing toast popups from obscuring bottom navigation icons or goal input fields.

## [1.2.3] - 2026-08-04

- **Complete AI Cleanup**: Removed the `Consistency Insight` card and unused AI helper components from `AnalyticsScreen.kt`. Removed the `AiCoachSheet` bottom sheet modal from `HomeScreen.kt`. All AI features are now strictly isolated to the dedicated **AI** bottom navigation tab (`AiScreen.kt`).

## [1.2.2] - 2026-08-04

- **Overview Hydration Widget Redesign**: Redesigned the Water Tracking card on `HomeScreen.kt` with a 10dp liquid progress bar, glowing header badge, daily goal progress percentage badge (`%`), large volume display, 3-cup quick log action chips (`+250 ml`, `+500 ml`, `+750 ml`), `-250 ml` subtract control, and one-tap reset.

## [1.2.1] - 2026-08-04

- **Gradle JDK Path Formatting**: Updated `org.gradle.java.home` path to use forward slashes (`C:/Users/aniru/.gradle/jdks/jetbrains_s_r_o_-21-amd64-windows.2`), aligning with registered IDE Java 21 JDK table entries and preventing Kotlin DSL build script semantic analysis warnings.

## [1.2.0] - 2026-08-04

- **Dedicated AI Bottom Tab**: Added new **AI** bottom navigation tab (`AiScreen.kt`), consolidating all AI capabilities (Camera AI Meal Scanner, Health Intelligence Engine, Daily Wellness Score, Contextual Insights, Today's Action Plan, 7-Day Digest, and Interactive Advice).
- **UI & Inset Realignment**: Removed AI components from `HomeScreen`, `AddFoodScreen`, and `AnalyticsScreen`. Added notch status bar insets (`statusBarsPadding()`) and bottom navigation bar padding across all screens for full phone display compatibility.

## [1.1.9] - 2026-08-04

- **Gradle JDK 21 Compatibility**: Configured `org.gradle.java.home` to point to RedHat Java 21 JDK, resolving Java 25 (class major version 69) Kotlin DSL semantic analysis errors in IDE build scripts.

## [1.1.8] - 2026-08-04

- **Camera AI Meal Scanner**: Point phone camera at meal, snap photo, and receive an instant on-device nutritional estimate (dish name, calories, protein, carbs, fat, fiber) with a fully editable review dialog before saving to Room DB (`CameraFoodEstimator.kt`).
- **Android Home Screen Widget**: AppWidgetProvider (`NourishAppWidget.kt` & `nourish_widget_layout.xml`) displaying daily Calorie intake, Water logged, and Step progress directly on the Android home screen.
- **Local Push Notifications**: Added `ReminderNotificationHelper.kt` for hydration & meal logging check-ins with a "Remind Hydrate" test trigger in Settings.
- **Tactile Haptic Feedback**: Integrated `LocalHapticFeedback.current` across button taps, step increments, water logging, AI Coach topic chips, and food log entries.
- **Fully Local & Personal**: 100% on-device local Room DB, no remote databases, APIs, or subscriptions required.

## [1.1.7] - 2026-08-04

- **AI Coach Tab**: Added "AI Coach" segment in Analytics screen (4th tab with AutoAwesome icon), powered by `HealthIntelligenceEngine` — a Whoop/Google Health-style on-device analytics engine.
- **Daily Wellness Score**: Full 0–100 wellness ring with Nutrition, Hydration, Sleep, and Activity sub-scores and yesterday delta indicator.
- **Personalized Insight Cards**: Up to 8 contextual, severity-color-coded insight cards (CRITICAL/WARNING/POSITIVE/INFO) covering sleep debt, protein gaps, hydration streaks, calorie surplus, and fiber deficiency.
- **Today's Action Plan**: 3 prioritized, concrete action items computed from current day metrics.
- **7-Day AI Summary**: Paragraph-length weekly analysis mirroring Google Health's weekly digest.
- **AnalyticsViewModel**: Added `aiCoachState: StateFlow<AiCoachReport>` combining all repositories (food, water, sleep, steps, settings).

## [1.1.6] - 2026-08-04

- **Animated Splash Screen**: Implemented brand dark theme (`#0B0E14` window background in `themes.xml`) and Jetpack Compose animated splash screen (`SplashScreen.kt`) with glowing pulse emblem, title entrance, and smooth 1.2-second transition into `HomeScreen`.

## [1.1.5] - 2026-08-04

- **Overview Header Scroll**: Removed nested Scaffold in `HomeScreen.kt` to fix header date and calorie dial scroll overlap.
- **Water Log Management**: Added `-250 ml` and `Clear Water` buttons to the water logging card in `HomeScreen.kt` and `HomeViewModel.kt`.
- **Hardware Sensor Night Mode Alignment**: Configured `WindowInsets(0,0,0,0)` and `navigationBarsPadding()` on `LiveSensorSleepSheet` for real phone device screen alignment.
- **More Screen Streamlining**: Removed Support & Information card, kept Google Health and Clear All Data as Quick Shortcuts, removed Danger Zone, and added app version footer (`Nourish Fitness v1.1.5 (Build 8)`).

## [1.1.4] - 2026-08-04

- **Android Studio JDK Table Alignment**: Configured `gradleJvm` to `jbr-21` (`C:/Users/aniru/.gradle/jdks/jetbrains_s_r_o_-21-amd64-windows.2`), matching Android Studio's registered `jdk.table.xml` entry.

## [1.1.3] - 2026-08-04

- **Gradle JVM Formatting**: Updated `org.gradle.java.home` to use Windows double backslash path syntax (`C:\\Users\\aniru\\.gradle\\jdks\\eclipse_adoptium-17-amd64-windows.2`) for IDE compatibility.

## [1.1.2] - 2026-08-04

- **Sleep Sheets Redesign**: Redesigned time picker wheel sheet (`PhoneClockWheelSheet`) and Hardware Sensor Night Mode sheet (`LiveSensorSleepSheet`) with glowing breathing pulse timer, depth gradient masks, and glassmorphism.
- **Hybrid AI Insights Engine**: Implemented cross-metric On-Device AI Health Reasoning Engine with time-of-day contextual analysis and interactive AI Health Coach sheet (`AiCoachSheet`).

## [1.1.1] - 2026-08-04

- **16 KB Page Size Support**: Upgraded CameraX dependencies (`camera-core`, `camera-camera2`, `camera-lifecycle`, `camera-view`) to 1.4.1 to align native libraries (`libimage_processing_util_jni.so`) to 16 KB page size boundaries for Android 15+ compatibility.

## [1.1.0] - 2026-08-04

### Added - AI Integration

- **GeminiNanoEngine**: On-device AI wrapper (MediaPipe LLM Inference) with streaming and single-shot generation; auto-discovers model at `/data/local/tmp/llm/model.bin`.
- **CameraCapture**: CameraX capture utility with `bindPreview`, `captureImageBitmap`, and `bitmapToInputTensor` (224×224 float RGB normalization).
- **LocalStorageHarvester**: Scoped Storage reader for `.txt` and `.json` logs from app-private `ai_logs/` directory; injects content as AI prompt context.
- **SensorTelemetryBinder**: SensorManager binding for Accelerometer, Step Counter, and Gyroscope with real-time `SharedFlow` and motion-state classification text.
- **AiPermissionsGate**: Multi-permission Compose composable requesting CAMERA, READ_MEDIA_IMAGES, BODY_SENSORS in a single launcher without blocking the UI thread.
- **AiContextAssembler**: Central prompt builder that merges sensor telemetry + storage logs + camera input into a unified Gemini Nano prompt.
- `AndroidManifest.xml`: Added CAMERA, READ_MEDIA_IMAGES, READ_EXTERNAL_STORAGE (API < 33), INTERNET, BODY_SENSORS; FileProvider for CameraX URI sharing.
- `file_provider_paths.xml`: FileProvider path config for captures and AI log directories.

## [1.0.1] - 2026-08-04

### Added - Health & Fitness Features

- Completed comprehensive food logging and nutrition tracking system with Indian and Kerala food database.
- Added sleep tracking screen with sleep score calculator and stage breakdowns (deep, REM, light, awake).
- Integrated analytics & trends module featuring calorie, water, sleep, and macro ratio charts.
- Updated settings management with user goals customization and data wipe/export features.
- Enhanced dark UI design system with frosted glass effects and dynamic progress indicators.
