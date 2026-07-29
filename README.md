FitnessApp

Android fitness tracking app (Food, Sleep, Settings, and Workout features).

Tech stack
- Kotlin
- Android Jetpack (Compose, ViewModel, Room)
- Gradle (Kotlin DSL)

Quick setup (Windows)
1. Install JDK 17 or later and Android Studio.
2. Set JAVA_HOME to your JDK install path (e.g., C:\Program Files\Java\jdk-17.0.x).
   - On Windows: set in System Environment Variables or run: setx JAVA_HOME "C:\Program Files\Java\jdk-17.0.x"
3. Open the project in Android Studio (open the `FitnessApp` folder) or use the Gradle wrapper from the command line.

Common commands (from project root: FitnessApp):
- Build: .\\gradlew.bat build
- Assemble debug: .\\gradlew.bat assembleDebug
- Run unit tests: .\\gradlew.bat test
- Run DAO/Instrumentation tests (requires Android Emulator or physical device): .\\gradlew.bat connectedAndroidTest

If using the provided helper script (Windows):
- scripts\\build.bat build

CI
A GitHub Actions workflow is provided at .github/workflows/android.yml that runs the Gradle build on push and pull requests.

Notes
- If the Gradle build fails locally with JAVA_HOME errors, make sure JAVA_HOME is set and the JDK is accessible on PATH.
- This repo contains existing Food and Sleep tracking features; the Workout feature has been added as a lightweight initial implementation.

Next steps you might want:
- Run the Gradle build locally or in CI
- Add instrumentation/UI tests
- Add feature to export/import data
- Add user authentication or cloud sync

Contact
If you want a specific enhancement (e.g., deeply-featured workout module, analytics, or CI matrix updates), tell me which and I'll implement it.