@echo off
echo ============================================
echo PostgreSQL Database Setup for Order Manager
echo ============================================
echo.

echo [INFO] This script will create the PostgreSQL databases for all environments
echo        - orderdb_dev (Development)
echo        - orderdb_test (Test)  
echo        - orderdb_prod (Production)
echo.

echo [PREREQ] Make sure PostgreSQL is installed and running on your system
echo.

set /p PGUSER="Enter PostgreSQL username (default: postgres): "
if "%PGUSER%"=="" set PGUSER=postgres

set /p PGHOST="Enter PostgreSQL host (default: localhost): "
if "%PGHOST%"=="" set PGHOST=localhost

set /p PGPORT="Enter PostgreSQL port (default: 5432): "
if "%PGPORT%"=="" set PGPORT=5432

echo.
echo [INFO] Connecting to PostgreSQL to create databases...
echo.

REM Execute the SQL script
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d postgres -f setup-postgresql.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo [SUCCESS] Databases created successfully!
    echo.
    echo Next steps:
    echo 1. Copy .env.template to .env
    echo 2. Update .env with your PostgreSQL credentials
    echo 3. Run the application with desired profile:
    echo    - Development: mvn spring-boot:run -Dspring-boot.run.profiles=dev
    echo    - Test: mvn spring-boot:run -Dspring-boot.run.profiles=test
    echo    - Production: mvn spring-boot:run -Dspring-boot.run.profiles=prod
    echo.
) else (
    echo.
    echo [ERROR] Failed to create databases. Please check:
    echo - PostgreSQL is running
    echo - User has sufficient privileges
    echo - Connection parameters are correct
    echo.
)

pause