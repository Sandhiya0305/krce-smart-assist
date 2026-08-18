@echo off
cd /d "%~dp0backend"
where mvn >nul 2>nul
if errorlevel 1 (
  echo Maven was not found. Install Maven or use your IDE's Maven support.
  pause
  exit /b 1
)
mvn spring-boot:run
