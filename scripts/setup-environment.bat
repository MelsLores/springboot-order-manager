@echo off
REM MELI Order Manager - Environment Setup Script for Windows Systems
REM This script sets up environment variables and starts the application with the specified profile

setlocal EnableDelayedExpansion

REM Set default values
set PROFILE=%1
set ACTION=%2

if "%PROFILE%"=="" set PROFILE=dev
if "%ACTION%"=="" set ACTION=start

REM Function to display usage
if "%PROFILE%"=="help" (
    echo.
    echo MELI Order Manager - Environment Setup
    echo =====================================
    echo Usage: %0 [PROFILE] [ACTION]
    echo.
    echo PROFILES:
    echo   dev     - Development environment ^(H2 database^)
    echo   test    - Testing environment ^(H2 database^)
    echo   prod    - Production environment ^(PostgreSQL^)
    echo.
    echo ACTIONS:
    echo   start   - Start the application ^(default^)
    echo   build   - Build the application only
    echo   clean   - Clean and build
    echo.
    echo Examples:
    echo   %0 dev start
    echo   %0 prod build
    echo   %0 test
    echo.
    goto :eof
)

REM Validate profile
if not "%PROFILE%"=="dev" if not "%PROFILE%"=="test" if not "%PROFILE%"=="prod" (
    echo Error: Invalid profile '%PROFILE%'
    echo Use '%0 help' for usage information
    exit /b 1
)

REM Validate action
if not "%ACTION%"=="start" if not "%ACTION%"=="build" if not "%ACTION%"=="clean" (
    echo Error: Invalid action '%ACTION%'
    echo Use '%0 help' for usage information
    exit /b 1
)

echo.
echo === MELI Order Manager - Sprint 2 Environment Setup ===
echo Profile: %PROFILE%
echo Action: %ACTION%
echo.

REM Set common environment variables
set SPRING_PROFILES_ACTIVE=%PROFILE%

REM Profile-specific environment variables
if "%PROFILE%"=="dev" (
    echo Setting up Development Environment...
    set DEV_SERVER_PORT=8080
    set DEV_DATABASE_URL=jdbc:postgresql://localhost:5432/orderdb_dev
    set DEV_DATABASE_USERNAME=postgres
    set DEV_DATABASE_PASSWORD=postgres
)

if "%PROFILE%"=="test" (
    echo Setting up Testing Environment...
    set TEST_SERVER_PORT=8081
    set TEST_DATABASE_URL=jdbc:postgresql://localhost:5432/orderdb_test
    set TEST_DATABASE_USERNAME=postgres
    set TEST_DATABASE_PASSWORD=postgres
)

if "%PROFILE%"=="prod" (
    echo Setting up Production Environment...
    REM Production environment variables ^(set these in your production server^)
    if not defined SERVER_PORT set SERVER_PORT=8080
    if not defined CONTEXT_PATH set CONTEXT_PATH=/api/v1
    
    REM Database configuration ^(REQUIRED for production^)
    if not defined PROD_DATABASE_URL set PROD_DATABASE_URL=jdbc:postgresql://localhost:5432/orderdb_prod
    if not defined PROD_DATABASE_USERNAME set PROD_DATABASE_USERNAME=postgres
    if not defined PROD_DATABASE_PASSWORD (
        echo Warning: PROD_DATABASE_PASSWORD environment variable is not set
        echo Set the following environment variables for production:
        echo   PROD_DATABASE_URL ^(default: jdbc:postgresql://localhost:5432/orderdb_prod^)
        echo   PROD_DATABASE_USERNAME ^(default: postgres^)
        echo   PROD_DATABASE_PASSWORD ^(REQUIRED^)
        echo   DB_POOL_SIZE ^(default: 20^)
        echo.
    )
    
    REM Logging configuration
    if not defined LOG_LEVEL_ROOT set LOG_LEVEL_ROOT=INFO
    if not defined LOG_LEVEL_APP set LOG_LEVEL_APP=INFO
    if not defined LOG_FILE_PATH set LOG_FILE_PATH=logs/order-manager-prod.log
)

REM Create logs directory
if not exist "logs" mkdir logs

REM Execute action
if "%ACTION%"=="clean" (
    echo Cleaning project...
    call mvn clean
    echo Building project...
    call mvn compile
)

if "%ACTION%"=="build" (
    echo Building project...
    call mvn clean compile
)

if "%ACTION%"=="start" (
    echo Building and starting application...
    call mvn clean compile
    echo Starting MELI Order Manager with profile: %PROFILE%
    call mvn spring-boot:run -Dspring-boot.run.profiles=%PROFILE%
)

echo Environment setup completed for profile: %PROFILE%
pause