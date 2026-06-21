@echo off
REM MediCare Hospital Management System - Windows Launcher

echo ============================================
echo   MediCare Hospital Management System
echo   Starting Application...
echo ============================================
echo.

REM Check if Maven is available
where mvn >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    echo Found Maven. Building and running...
    call mvn clean javafx:run
) else (
    echo Maven not found. Trying to run pre-built JAR...
    
    REM Check if JAR exists
    if exist "target\hospital-management-system-1.0.0-all.jar" (
        java -jar target\hospital-management-system-1.0.0-all.jar
    ) else (
        echo.
        echo ERROR: No JAR file found and Maven is not installed.
        echo.
        echo Please install Maven 3.8+ or build the project manually.
        echo.
        pause
        exit /b 1
    )
)

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Application exited with error code %ERRORLEVEL%
    pause
)
