@echo off
echo AI Resume Screening Tool - Quick Database Setup
echo ============================================
echo.

REM Check if PostgreSQL is running
echo Checking PostgreSQL connection...
psql -h localhost -p 5432 -U postgres -c "SELECT version();" >nul 2>&1

if %errorlevel% neq 0 (
    echo ERROR: Cannot connect to PostgreSQL!
    echo.
    echo Please ensure:
    echo 1. PostgreSQL is installed and running
    echo 2. PostgreSQL service is started
    echo 3. Username 'postgres' exists with password '1234'
    echo.
    echo To install PostgreSQL:
    echo - Download from: https://www.postgresql.org/download/windows/
    echo - Or use: winget install PostgreSQL.PostgreSQL
    echo.
    pause
    exit /b 1
)

echo PostgreSQL is running!
echo.

REM Create database if it doesn't exist
echo Creating database 'rst_db'...
psql -h localhost -p 5432 -U postgres -c "CREATE DATABASE rst_db;" 2>nul

if %errorlevel% equ 0 (
    echo Database 'rst_db' created successfully!
) else (
    echo Database 'rst_db' already exists or creation failed.
    echo Continuing with existing database...
)

echo.
echo Database setup completed!
echo.
echo You can now start the Spring Boot application:
echo   mvn spring-boot:run
echo.
echo The application will automatically create tables using Flyway migrations.
echo.
pause
