# Changelog

## [1.5.2] - 2026-08-05

- **Google Health Connect Data Sync Fixes**: Added missing `insertStepsRecords` to push step telemetry to Health Connect; updated all records to use system `ZoneOffset` instead of UTC to prevent timestamp shifting; enabled full historical sync of food, water, sleep, and step logs in `SettingsViewModel`.

## [1.0.0] - 2026-08-04

- **Official Release v1.0.0**: Production release of Nourish Fitness app featuring live OpenFoodFacts food search & barcode matching, Google Health Connect SDK integration, on-device AI Wellness Coach, interactive trends & analytics, background hardware step tracker, and local JSON/CSV data export.
- **Git Repo Cleanup**: Cleaned build artifacts from repository index and updated `.gitignore` for clean open-source repository distribution.

## [1.5.1] - 2026-08-04

- **Cleaned Wearable Dependencies**: Removed wearable-specific telemetry readers (`HeartRateRecord`, `readWearableSteps`, `readWearableSleep`) from `HealthConnectManager.kt`.
- **Connected Analytics Screen**: Added Quick Action "Analytics" chip in `HomeScreen.kt` to make `AnalyticsScreen` accessible via `NavGraph`.
- **Health Data Export (JSON/CSV)**: Added JSON & CSV log exporter in `SettingsViewModel.kt` and `SettingsScreen.kt`.
- **Hardware Step Counter Service Boot Receiver**: Created `BootCompletedReceiver.kt` to auto-start `StepTrackingService` on device boot completion (`ACTION_BOOT_COMPLETED`).
- **Interactive AI Action Plan**: Made AI Action Plan items interactive with visual completion state toggles in `AiScreen.kt`.

## [1.5.0] - 2026-08-04

- **OpenFoodFacts Live API Search**: Integrated live REST API food product search in `AddFoodScreen.kt`. Search any packaged food or barcode, fetch live nutrition info (Calories, Protein, Carbs, Fat, Brand), select items, and save directly to local Room DB food log.
- **Biometric & Wearable Sync**: Enhanced `HealthConnectManager.kt` with bi-directional wearable sync capabilities (`StepsRecord`, `SleepSessionRecord`, `HeartRateRecord`). Enables reading smartwatch telemetry (Galaxy Watch, Apple Watch, Pixel Watch, Oura via Health Connect) into app database.

## [1.4.3] - 2026-08-04

- **Manifest cleanup**: Removed `CAMERA`, `READ_MEDIA_IMAGES`, `READ_EXTERNAL_STORAGE` permissions — no longer needed since camera scanner was removed
- **Health Connect rationale**: Added `ViewPermissionUsageActivity` alias required by Google Play for apps using health permissions (opens privacy rationale screen from Health Connect settings)
- Removed orphaned `FileProvider` block (was only needed for CameraX file sharing)

## [1.4.2] - 2026-08-04

- **Bottom Nav Streamlined**: Reduced from 6 to 4 tabs (Home, Nutrition, Sleep, Settings) for iOS-style minimal navigation
- **Camera Removed**: Cleaned all `CameraFoodEstimator`, `PhotoCamera`, `QrCodeScanner` imports from `AddFoodScreen.kt`
- **Health Connect Sync Fixed**: `syncToHealthConnect()` in `SettingsViewModel` now fetches real Room DB data — food, water, sleep — before writing to Google Health Connect; permission dialog fires first
- **Sleep Purple Fixed**: Changed `AccentPurple` from harsh `#D500F9` neon to soft iOS indigo `#7B61FF`
- **Bevel Card Depth**: `AppCard` now uses `.shadow(elevation=8dp)` + stronger `0.28f` white highlight for true depth
- **Real Streak Counter**: `HomeViewModel` computes consecutive logged days from food entries; replaces hardcoded "7 Days"
- **Health Connect Status Dot**: Live green/grey dot on Home header showing whether Health Connect is installed
- **Nutrition FAB**: Added green `FloatingActionButton` on `FoodLogScreen` for quick food entry
- **7-Day Sleep Chart**: Added weekly bar chart at top of Sleep tab using existing `weeklySleepDays` data from `SleepViewModel`

## [1.4.1] - 2026-08-04

- **Google Health Connect In-App Permission Prompt**: Integrated `PermissionController.createRequestPermissionResultContract()` launcher with `READ_NUTRITION` and `WRITE_NUTRITION` manifest declarations in `AndroidManifest.xml` and `SettingsScreen.kt` for instant interactive permission prompting.

## [1.4.0] - 2026-08-04

- **Edge-to-Edge Frosted Glass Navigation Bar**: Redesigned bottom navigation bar with Apple Fitness inspired edge-to-edge translucent frosted glass background (`SurfaceCard 92%`), 1dp subtle top border (`Color.White 10%`), animated icon scale specifications (`20.dp` to `24.dp`), glowing active pill backdrops, and native Android `navigationBarsPadding()` support.

## [1.3.9] - 2026-08-04

- **Bottom Navigation Capsule Bar**: Rebuilt bottom navigation bar with pixel-perfect custom floating capsule layout (`68.dp` height, `34.dp` rounded corners, `22.dp` crisp icons, `11.sp` labels, and emerald pill active tab glows) preventing label cutoff and clipping across all screen densities.

## [1.3.8] - 2026-08-04

- **Ultra-Premium Design Overhaul**: Redesigned theme color tokens with space dark surfaces (`#0B0E14`), ultra-vibrant neon accents (`#00E676` Emerald, `#00B0FF` Cyan, `#FF6D00` Amber), floating translucent navigation capsule bar with pill indicator glows, and elevated hero header with 7-Day streak badge.

## [1.3.7] - 2026-08-04

- **Native Google Health Connect Client SDK Writer**: Integrated `androidx.health.connect:connect-client:1.1.0-alpha10` and compiled against Android 35 SDK. Implemented `insertNutritionRecords`, `insertHydrationRecords`, and `insertSleepRecords` to write local Room DB entries directly to Google Health Connect API.

## [1.3.6] - 2026-08-04

- **Home Screen Quick Action Bar**: Added interactive quick action chips (`+ Meal`, `+ Sleep`, `AI Hub`) to `HomeScreen.kt` connected directly to `NavGraph` routes for one-tap navigation.

## [1.3.5] - 2026-08-04

- **Google Health Connect Multi-Intent Fallback & Telemetry Export**: Upgraded `HealthConnectManager.kt` with multi-intent settings resolution (`android.provider.Settings.ACTION_HEALTH_CONNECT_SETTINGS`, `android.health.connect.action.HEALTH_CONNECT_SETTINGS`, `androidx.health.ACTION_HEALTH_CONNECT_SETTINGS`, deep links, and package launch intents) and `exportHealthData` JSON payload formatter.

## [1.3.4] - 2026-08-04

- **Android 15+ 16 KB Page Alignment Compliance**: Upgraded `com.google.mediapipe:tasks-genai` to `0.10.20` and configured `packaging.jniLibs.useLegacyPackaging = false` in `app/build.gradle.kts` to ensure uncompressed, 16 KB (0x4000) page-aligned `libllm_inference_engine_jni.so` native binaries.

## [1.3.3] - 2026-08-04

- **Transparent Status Bar Edge-to-Edge**: Configured `@android:color/transparent` status and navigation bar in `themes.xml` and `SystemBarStyle.dark(TRANSPARENT)` in `MainActivity.kt`, eliminating top black bars when scrolling down on every screen.
- **Redesigned AI Health Hub (`AiScreen.kt`)**: Implemented live interactive prompt query bar, 0-100 Wellness Score ring with 4 sub-scores, contextual AI insight cards with severity indicators, and today's action plan checklist.
- **Redesigned More & Account Hub (`SettingsScreen.kt`)**: Created interactive goal adjusters (calories, water, sleep, steps), Google Health Connect launcher, hardware step sensor status, and push notification trigger.

## [1.3.2] - 2026-08-04

- **Edge-to-Edge Status Bar Inset Realignment**: Configured `contentWindowInsets = WindowInsets(0, 0, 0, 0)` across root and child `Scaffold` containers (`NavGraph.kt`, `HomeScreen.kt`, `FoodLogScreen.kt`, `SleepLogScreen.kt`, `AiScreen.kt`, `AnalyticsScreen.kt`, `SettingsScreen.kt`, `AddFoodScreen.kt`, `AddSleepScreen.kt`, `NutritionDetailsScreen.kt`), eliminating duplicate status bar top gaps and top bar scrolling cutoffs.

## [1.3.1] - 2026-08-04

- **Live OpenFoodFacts REST API Lookup**: Added `lookupBarcodeOnline` and JVM-compatible JSON parser to `BarcodeScannerUtil.kt` (`BarcodeScannerUtilTest.kt`).
- **Scheduled Background Reminders**: Created `ReminderReceiver.kt` and updated `ReminderNotificationHelper.kt` with `AlarmManager` repeating daily alarms (8 AM Breakfast, 2 PM Hydration, 10 PM Sleep).
- **Google Health Connect Record Schemas**: Added `buildNutritionRecordJson`, `buildHydrationRecordJson`, and `buildSleepSessionRecordJson` to `HealthConnectManager.kt`.
- **Multi-Region ML Food Estimator**: Upgraded `CameraFoodEstimator.kt` with $3 \times 3$ grid spectrum sampling and expanded food categories.

## [1.3.0] - 2026-08-04

- **AI Voice Speech Recognition & TTS**: Created `VoiceSpeechManager.kt` supporting Speech-To-Text voice logging intents and Text-To-Speech audio response playback (`VoiceSpeechManagerTest.kt`).

## [1.2.9] - 2026-08-04

- **Hardware Step Tracking Foreground Service**: Created `StepTrackingService.kt` for background physical step counter sensor event processing (`Sensor.TYPE_STEP_COUNTER`), verified by `StepTrackingServiceTest.kt`.

## [1.2.8] - 2026-08-04

- **Live Gemini AI Engine**: Verified structured context prompt formatting and inference engine fallbacks (`GeminiNanoEngineTest.kt`).

## [1.2.7] - 2026-08-04

- **Barcode Food Scanner & OpenFoodFacts Lookup**: Added `BarcodeScannerUtil.kt` for EAN-13/UPC barcode string parsing and OpenFoodFacts product dataset matching, backed by `BarcodeScannerUtilTest.kt`.

## [1.2.6] - 2026-08-04

- **Camera ML Meal Recognition**: Enhanced `CameraFoodEstimator.kt` with RGB channel luminance scoring and dynamic confidence metrics for bitmap analysis, verified by `CameraFoodEstimatorTest.kt`.

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
