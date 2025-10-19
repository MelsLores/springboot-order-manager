#!/bin/bash

# MELI Order Manager - PostgreSQL Startup Script
# This script starts the application with PostgreSQL configuration

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}=====================================${NC}"
echo -e "${GREEN}  MELI Order Manager - PostgreSQL${NC}"
echo -e "${GREEN}=====================================${NC}"
echo ""

# Check if PostgreSQL is running
echo -e "${YELLOW}Checking PostgreSQL connection...${NC}"
if command -v pg_isready &> /dev/null; then
    if ! pg_isready -h localhost -p 5432 &> /dev/null; then
        echo -e "${RED}WARNING: PostgreSQL might not be running on localhost:5432${NC}"
        echo "Please make sure PostgreSQL is started before running the application"
        echo ""
        echo "You can start PostgreSQL with:"
        echo "  - Ubuntu/Debian: sudo systemctl start postgresql"
        echo "  - CentOS/RHEL: sudo systemctl start postgresql"
        echo "  - macOS (Homebrew): brew services start postgresql"
        echo ""
        read -p "Do you want to continue anyway? (y/N): " -n 1 -r
        echo ""
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            exit 1
        fi
    fi
else
    echo -e "${YELLOW}pg_isready not found. Assuming PostgreSQL is available.${NC}"
fi

echo ""
echo -e "${GREEN}Starting MELI Order Manager with PostgreSQL...${NC}"
echo -e "${YELLOW}Database: PostgreSQL (localhost:5432)${NC}"
echo -e "${YELLOW}Profile: Production ready${NC}"
echo ""

# Set environment variables for PostgreSQL
export DATABASE_URL="jdbc:postgresql://localhost:5432/postgres"
export DATABASE_USERNAME="postgres"
export DATABASE_PASSWORD="postgres"
export SERVER_PORT="8080"
export CONTEXT_PATH="/api/v1"

# Start the application
echo -e "${YELLOW}Building application...${NC}"
mvn clean compile
if [ $? -ne 0 ]; then
    echo -e "${RED}Build failed! Please check the errors above.${NC}"
    exit 1
fi

echo ""
echo -e "${GREEN}Application compiled successfully!${NC}"
echo -e "${YELLOW}Starting Spring Boot application...${NC}"
echo ""

mvn spring-boot:run

echo ""
echo -e "${GREEN}Application stopped.${NC}"