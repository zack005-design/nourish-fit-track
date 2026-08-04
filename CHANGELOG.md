# Changelog

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
