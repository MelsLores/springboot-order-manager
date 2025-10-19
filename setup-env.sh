#!/bin/bash

# MELI Order Manager - Environment Setup Script for Unix/Linux
# This script helps configure and run the application in different environments

echo "==============================================="
echo "   MELI Order Manager - Environment Setup"
echo "==============================================="
echo ""

show_menu() {
    echo "Choose environment profile:"
    echo "1. Development (dev)"
    echo "2. Testing (test)"
    echo "3. Production (prod)"
    echo "4. Custom profile"
    echo "5. Exit"
    echo ""
    read -p "Enter your choice (1-5): " choice
}

setup_dev() {
    echo ""
    echo "Setting up DEVELOPMENT environment..."
    export SPRING_PROFILES_ACTIVE=dev
    export DEV_DATABASE_URL=jdbc:postgresql://localhost:5432/postgres
    export DEV_DATABASE_USERNAME=postgres
    export DEV_DATABASE_PASSWORD=postgres
    export DEV_SERVER_PORT=8080
    echo "Environment variables set for DEVELOPMENT profile."
    show_actions
}

setup_test() {
    echo ""
    echo "Setting up TESTING environment..."
    export SPRING_PROFILES_ACTIVE=test
    export TEST_DATABASE_URL=jdbc:postgresql://localhost:5432/orderdb_test
    export TEST_DATABASE_USERNAME=postgres
    export TEST_DATABASE_PASSWORD=postgres
    export TEST_SERVER_PORT=8081
    echo "Environment variables set for TESTING profile."
    show_actions
}

setup_prod() {
    echo ""
    echo "Setting up PRODUCTION environment..."
    echo "WARNING: Make sure all production environment variables are properly configured!"
    export SPRING_PROFILES_ACTIVE=prod
    echo "Production profile activated. Please ensure all PROD_* variables are set."
    show_actions
}

setup_custom() {
    echo ""
    read -p "Enter custom profile name: " custom_profile
    export SPRING_PROFILES_ACTIVE=$custom_profile
    echo "Custom profile '$custom_profile' activated."
    show_actions
}

show_actions() {
    echo ""
    echo "Choose action:"
    echo "1. Run application"
    echo "2. Run tests"
    echo "3. Clean and compile"
    echo "4. Package application"
    echo "5. Back to main menu"
    echo ""
    read -p "Enter your choice (1-5): " action
    
    case $action in
        1) run_app ;;
        2) run_tests ;;
        3) clean_compile ;;
        4) package_app ;;
        5) main_menu ;;
        *) echo "Invalid choice. Please try again."; show_actions ;;
    esac
}

run_app() {
    echo ""
    echo "Starting Order Manager Application with profile: $SPRING_PROFILES_ACTIVE"
    echo ""
    mvn spring-boot:run -Dspring-boot.run.profiles=$SPRING_PROFILES_ACTIVE
    main_menu
}

run_tests() {
    echo ""
    echo "Running tests with profile: $SPRING_PROFILES_ACTIVE"
    echo ""
    mvn test -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE
    main_menu
}

clean_compile() {
    echo ""
    echo "Cleaning and compiling project..."
    echo ""
    mvn clean compile
    main_menu
}

package_app() {
    echo ""
    echo "Packaging application..."
    echo ""
    mvn clean package -DskipTests
    main_menu
}

main_menu() {
    echo ""
    echo "Operation completed."
    echo ""
    show_menu
    
    case $choice in
        1) setup_dev ;;
        2) setup_test ;;
        3) setup_prod ;;
        4) setup_custom ;;
        5) echo "Goodbye!"; exit 0 ;;
        *) echo "Invalid choice. Please try again."; main_menu ;;
    esac
}

# Make script executable
chmod +x "$0" 2>/dev/null || true

# Start the script
main_menu