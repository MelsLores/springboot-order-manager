@echo off
REM MELI Order Manager - Environment Setup Script for Windows
REM This script helps configure and run the application in different environments

echo ===============================================
echo   MELI Order Manager - Environment Setup
echo ===============================================
echo.

:menu
echo Choose environment profile:
echo 1. Development (dev)
echo 2. Testing (test) 
echo 3. Production (prod)
echo 4. Custom profile
echo 5. Exit
echo.
set /p choice="Enter your choice (1-5): "

if "%choice%"=="1" goto dev
if "%choice%"=="2" goto test
if "%choice%"=="3" goto prod
if "%choice%"=="4" goto custom
if "%choice%"=="5" goto exit
goto invalid

:dev
echo.
echo Setting up DEVELOPMENT environment...
set SPRING_PROFILES_ACTIVE=dev
set DEV_DATABASE_URL=jdbc:postgresql://localhost:5432/postgres
set DEV_DATABASE_USERNAME=postgres
set DEV_DATABASE_PASSWORD=postgres
set DEV_SERVER_PORT=8080
echo Environment variables set for DEVELOPMENT profile.
goto run

:test
echo.
echo Setting up TESTING environment...
set SPRING_PROFILES_ACTIVE=test
set TEST_DATABASE_URL=jdbc:postgresql://localhost:5432/orderdb_test
set TEST_DATABASE_USERNAME=postgres
set TEST_DATABASE_PASSWORD=postgres
set TEST_SERVER_PORT=8081
echo Environment variables set for TESTING profile.
goto run

:prod
echo.
echo Setting up PRODUCTION environment...
echo WARNING: Make sure all production environment variables are properly configured!
set SPRING_PROFILES_ACTIVE=prod
echo Production profile activated. Please ensure all PROD_* variables are set.
goto run

:custom
echo.
set /p custom_profile="Enter custom profile name: "
set SPRING_PROFILES_ACTIVE=%custom_profile%
echo Custom profile '%custom_profile%' activated.
goto run

:run
echo.
echo Choose action:
echo 1. Run application
echo 2. Run tests
echo 3. Clean and compile
echo 4. Package application
echo 5. Back to main menu
echo.
set /p action="Enter your choice (1-5): "

if "%action%"=="1" goto run_app
if "%action%"=="2" goto run_tests
if "%action%"=="3" goto clean_compile
if "%action%"=="4" goto package
if "%action%"=="5" goto menu
goto invalid

:run_app
echo.
echo Starting Order Manager Application with profile: %SPRING_PROFILES_ACTIVE%
echo.
mvn spring-boot:run -Dspring-boot.run.profiles=%SPRING_PROFILES_ACTIVE%
goto end

:run_tests
echo.
echo Running tests with profile: %SPRING_PROFILES_ACTIVE%
echo.
mvn test -Dspring.profiles.active=%SPRING_PROFILES_ACTIVE%
goto end

:clean_compile
echo.
echo Cleaning and compiling project...
echo.
mvn clean compile
goto menu

:package
echo.
echo Packaging application...
echo.
mvn clean package -DskipTests
goto end

:invalid
echo.
echo Invalid choice. Please try again.
echo.
goto menu

:end
echo.
echo Operation completed.
pause
goto menu

:exit
echo.
echo Goodbye!
exit /b 0