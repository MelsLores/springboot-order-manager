#!/bin/bash

# MELI Order Manager - Environment Setup Script for Unix/Linux Systems
# This script sets up environment variables and starts the application with the specified profile

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to display usage
show_usage() {
    echo -e "${BLUE}MELI Order Manager - Environment Setup${NC}"
    echo -e "${YELLOW}Usage: $0 [PROFILE] [ACTION]${NC}"
    echo ""
    echo "PROFILES:"
    echo "  dev     - Development environment (H2 database)"
    echo "  test    - Testing environment (H2 database)"
    echo "  prod    - Production environment (PostgreSQL)"
    echo ""
    echo "ACTIONS:"
    echo "  start   - Start the application (default)"
    echo "  build   - Build the application only"
    echo "  clean   - Clean and build"
    echo ""
    echo "Examples:"
    echo "  $0 dev start"
    echo "  $0 prod build"
    echo "  $0 test"
}

# Set default values
PROFILE=${1:-dev}
ACTION=${2:-start}

# Validate profile
if [[ ! "$PROFILE" =~ ^(dev|test|prod)$ ]]; then
    echo -e "${RED}Error: Invalid profile '$PROFILE'${NC}"
    show_usage
    exit 1
fi

# Validate action
if [[ ! "$ACTION" =~ ^(start|build|clean)$ ]]; then
    echo -e "${RED}Error: Invalid action '$ACTION'${NC}"
    show_usage
    exit 1
fi

echo -e "${BLUE}=== MELI Order Manager - Sprint 2 Environment Setup ===${NC}"
echo -e "${YELLOW}Profile: $PROFILE${NC}"
echo -e "${YELLOW}Action: $ACTION${NC}"
echo ""

# Set common environment variables
export SPRING_PROFILES_ACTIVE=$PROFILE

# Profile-specific environment variables
case $PROFILE in
    "dev")
        echo -e "${GREEN}Setting up Development Environment...${NC}"
        export DEV_SERVER_PORT=8080
        export DEV_DATABASE_URL="jdbc:postgresql://localhost:5432/orderdb_dev"
        export DEV_DATABASE_USERNAME="postgres"
        export DEV_DATABASE_PASSWORD="postgres"
        ;;
    "test")
        echo -e "${GREEN}Setting up Testing Environment...${NC}"
        export TEST_SERVER_PORT=8081
        export TEST_DATABASE_URL="jdbc:postgresql://localhost:5432/orderdb_test"
        export TEST_DATABASE_USERNAME="postgres"
        export TEST_DATABASE_PASSWORD="postgres"
        ;;
    "prod")
        echo -e "${GREEN}Setting up Production Environment...${NC}"
        # Production environment variables (set these in your production server)
        export SERVER_PORT=${SERVER_PORT:-8080}
        export CONTEXT_PATH=${CONTEXT_PATH:-/api/v1}
        
        # Database configuration (REQUIRED for production)
        if [ -z "$DATABASE_PASSWORD" ]; then
            echo -e "${RED}Warning: DATABASE_PASSWORD environment variable is not set${NC}"
            echo -e "${YELLOW}Set the following environment variables for production:${NC}"
            echo "  DATABASE_URL (default: jdbc:postgresql://localhost:5432/orderdb)"
            echo "  DATABASE_USERNAME (default: postgres)"
            echo "  DATABASE_PASSWORD (REQUIRED)"
            echo "  DB_POOL_SIZE (default: 20)"
            echo ""
        fi
        
        # Logging configuration
        export LOG_LEVEL_ROOT=${LOG_LEVEL_ROOT:-INFO}
        export LOG_LEVEL_APP=${LOG_LEVEL_APP:-INFO}
        export LOG_FILE_PATH=${LOG_FILE_PATH:-logs/order-manager-prod.log}
        ;;
esac

# Create logs directory
mkdir -p logs

# Execute action
case $ACTION in
    "clean")
        echo -e "${YELLOW}Cleaning project...${NC}"
        mvn clean
        echo -e "${YELLOW}Building project...${NC}"
        mvn compile
        ;;
    "build")
        echo -e "${YELLOW}Building project...${NC}"
        mvn clean compile
        ;;
    "start")
        echo -e "${YELLOW}Building and starting application...${NC}"
        mvn clean compile
        echo -e "${GREEN}Starting MELI Order Manager with profile: $PROFILE${NC}"
        mvn spring-boot:run -Dspring-boot.run.profiles=$PROFILE
        ;;
esac

echo -e "${GREEN}Environment setup completed for profile: $PROFILE${NC}"