#!/bin/bash

echo "============================================"
echo "PostgreSQL Database Setup for Order Manager"
echo "============================================"
echo

echo "[INFO] This script will create the PostgreSQL databases for all environments"
echo "       - orderdb_dev (Development)"
echo "       - orderdb_test (Test)"  
echo "       - orderdb_prod (Production)"
echo

echo "[PREREQ] Make sure PostgreSQL is installed and running on your system"
echo

# Get PostgreSQL connection parameters
read -p "Enter PostgreSQL username (default: postgres): " PGUSER
PGUSER=${PGUSER:-postgres}

read -p "Enter PostgreSQL host (default: localhost): " PGHOST
PGHOST=${PGHOST:-localhost}

read -p "Enter PostgreSQL port (default: 5432): " PGPORT
PGPORT=${PGPORT:-5432}

echo
echo "[INFO] Connecting to PostgreSQL to create databases..."
echo

# Execute the SQL script
psql -h "$PGHOST" -p "$PGPORT" -U "$PGUSER" -d postgres -f setup-postgresql.sql

if [ $? -eq 0 ]; then
    echo
    echo "[SUCCESS] Databases created successfully!"
    echo
    echo "Next steps:"
    echo "1. Copy .env.template to .env"
    echo "2. Update .env with your PostgreSQL credentials"
    echo "3. Run the application with desired profile:"
    echo "   - Development: mvn spring-boot:run -Dspring-boot.run.profiles=dev"
    echo "   - Test: mvn spring-boot:run -Dspring-boot.run.profiles=test"
    echo "   - Production: mvn spring-boot:run -Dspring-boot.run.profiles=prod"
    echo
else
    echo
    echo "[ERROR] Failed to create databases. Please check:"
    echo "- PostgreSQL is running"
    echo "- User has sufficient privileges"
    echo "- Connection parameters are correct"
    echo
fi