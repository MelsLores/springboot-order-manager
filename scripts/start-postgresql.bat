@echo off
REM MELI Order Manager - PostgreSQL Startup Script
REM This script starts the application with PostgreSQL configuration

echo.
echo =====================================
echo   MELI Order Manager - PostgreSQL
echo =====================================
echo.

REM Check if PostgreSQL is running
echo Checking PostgreSQL connection...
pg_isready -h localhost -p 5432 > nul 2>&1
if %errorlevel% neq 0 (
    echo WARNING: PostgreSQL might not be running on localhost:5432
    echo Please make sure PostgreSQL is started before running the application
    echo.
    echo You can start PostgreSQL with:
    echo   - Windows Service: net start postgresql-x64-13
    echo   - Manual: pg_ctl start -D "C:\Program Files\PostgreSQL\13\data"
    echo.
    choice /c YN /m "Do you want to continue anyway"
    if errorlevel 2 exit /b 1
)

echo.
echo Starting MELI Order Manager with PostgreSQL...
echo Database: PostgreSQL (localhost:5432)
echo Profile: Production ready
echo.

REM Set environment variables for PostgreSQL
set DATABASE_URL=jdbc:postgresql://localhost:5432/postgres
set DATABASE_USERNAME=postgres
set DATABASE_PASSWORD=postgres
set SERVER_PORT=8080
set CONTEXT_PATH=/api/v1

REM Start the application
mvn clean compile
if %errorlevel% neq 0 (
    echo Build failed! Please check the errors above.
    pause
    exit /b 1
)

echo.
echo Application compiled successfully!
echo Starting Spring Boot application...
echo.

mvn spring-boot:run

echo.
echo Application stopped.
pause