@echo off
echo ===================================
echo Restarting Flower Manager Backend
echo ===================================

cd /d "%~dp0"

echo.
echo Step 1: Stopping existing Java processes...
taskkill /f /im java.exe 2>nul
timeout /t 2 /nobreak >nul

echo.
echo Step 2: Starting Spring Boot application...
start "Flower Manager Backend" cmd /k "mvnw.cmd spring-boot:run"

echo.
echo Backend is starting in a new window!
echo Wait for "Started FlowerManagerApplication" message.
echo.
pause
