@echo off
REM Simple developer helper to run Gradle build on Windows. Exits with non-zero when JAVA_HOME is not set.
if "%JAVA_HOME%"=="" (
  echo ERROR: JAVA_HOME is not set. Please set JAVA_HOME to your JDK installation path and reopen the shell.
  exit /b 1
)
REM Run Gradle wrapper from project root (scripts is inside the project)
cd /d "%~dp0\.."
echo Running Gradle with arguments: %*
.\gradlew.bat %*
exit /b %ERRORLEVEL%