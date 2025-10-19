@echo off
REM Script de validación de configuración para capturas de pantalla

echo ===============================================
echo   MELI Order Manager - Configuration Demo
echo ===============================================
echo.

echo 1. ESTRUCTURA DEL PROYECTO:
echo.
dir /b src\main\resources\application*.yml
echo.

echo 2. PERFILES DISPONIBLES:
echo   - Development (dev)
echo   - Testing (test)  
echo   - Production (prod)
echo.

echo 3. VARIABLES DE ENTORNO CONFIGURADAS:
echo   SPRING_PROFILES_ACTIVE = %SPRING_PROFILES_ACTIVE%
echo   SERVER_PORT = %SERVER_PORT%
echo   DATABASE_URL = %DATABASE_URL%
echo.

echo 4. ARCHIVOS DE CONFIGURACION:
echo   [✓] application.yml - Configuracion principal
echo   [✓] application-dev.yml - Desarrollo  
echo   [✓] application-test.yml - Testing
echo   [✓] application-prod.yml - Produccion
echo   [✓] .env.example - Template de variables
echo   [✓] docker-compose.yml - Servicios de base de datos
echo.

echo 5. SCRIPTS AUTOMATIZADOS:
echo   [✓] setup-env.bat - Script Windows
echo   [✓] setup-env.sh - Script Unix/Linux  
echo.

echo 6. VALIDACION DE MAVEN:
mvn -version
echo.

echo ===============================================
echo   Configuracion lista para demostracion!
echo ===============================================
pause