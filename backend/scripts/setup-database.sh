#!/bin/bash

# Database Setup Script for AI Resume Screening Tool
# This script creates the PostgreSQL database and user if they don't exist

echo "Setting up PostgreSQL database for AI Resume Screening Tool..."

# Database configuration
DB_NAME="rst_db"
DB_USER="postgres"
DB_PASSWORD="1234"
DB_HOST="localhost"
DB_PORT="5432"

# Check if PostgreSQL is running
echo "Checking PostgreSQL service..."
pg_isready -h $DB_HOST -p $DB_PORT
if [ $? -ne 0 ]; then
    echo "Error: PostgreSQL is not running. Please start PostgreSQL service first."
    exit 1
fi

echo "PostgreSQL is running."

# Create database if it doesn't exist
echo "Creating database '$DB_NAME' if it doesn't exist..."
psql -h $DB_HOST -p $DB_PORT -U $DB_USER -tc "SELECT 1 FROM pg_database WHERE datname = '$DB_NAME'" | grep -q 1 || psql -h $DB_HOST -p $DB_PORT -U $DB_USER -c "CREATE DATABASE $DB_NAME"

if [ $? -eq 0 ]; then
    echo "Database '$DB_NAME' is ready."
else
    echo "Error: Failed to create database '$DB_NAME'."
    exit 1
fi

# Test database connection
echo "Testing database connection..."
psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -c "SELECT version();" > /dev/null

if [ $? -eq 0 ]; then
    echo "Database connection successful."
else
    echo "Error: Failed to connect to database '$DB_NAME'."
    exit 1
fi

echo "Database setup completed successfully!"
echo ""
echo "Database Details:"
echo "  Host: $DB_HOST"
echo "  Port: $DB_PORT"
echo "  Database: $DB_NAME"
echo "  User: $DB_USER"
echo ""
echo "You can now run 'mvn flyway:migrate' to apply database migrations."
echo "Or start the Spring Boot application to automatically run migrations."
