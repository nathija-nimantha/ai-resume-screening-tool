@echo off
REM Database Setup Script for AI Resume Screening Tool (Windows)
REM This script creates the PostgreSQL database and user if they don't exist

echo Setting up PostgreSQL database for AI Resume Screening Tool...

REM Database configuration
set DB_NAME=rst_db
set DB_USER=postgres
set DB_PASSWORD=1234
set DB_HOST=localhost
set DB_PORT=5432

REM Check if PostgreSQL is running
echo Checking PostgreSQL service...
pg_isready -h %DB_HOST% -p %DB_PORT%
if %errorlevel% neq 0 (
    echo Error: PostgreSQL is not running. Please start PostgreSQL service first.
    pause
    exit /b 1
)

echo PostgreSQL is running.

REM Create database if it doesn't exist
echo Creating database '%DB_NAME%' if it doesn't exist...
psql -h %DB_HOST% -p %DB_PORT% -U %DB_USER% -tc "SELECT 1 FROM pg_database WHERE datname = '%DB_NAME%'" | findstr "1" >nul
if %errorlevel% neq 0 (
    psql -h %DB_HOST% -p %DB_PORT% -U %DB_USER% -c "CREATE DATABASE %DB_NAME%"
)

if %errorlevel% equ 0 (
    echo Database '%DB_NAME%' is ready.
) else (
    echo Error: Failed to create database '%DB_NAME%'.
    pause
    exit /b 1
)

REM Test database connection
echo Testing database connection...
psql -h %DB_HOST% -p %DB_PORT% -U %DB_USER% -d %DB_NAME% -c "SELECT version();" >nul

if %errorlevel% equ 0 (
    echo Database connection successful.
) else (
    echo Error: Failed to connect to database '%DB_NAME%'.
    pause
    exit /b 1
)

echo Database setup completed successfully!
echo.
echo Database Details:
echo   Host: %DB_HOST%
echo   Port: %DB_PORT%
echo   Database: %DB_NAME%
echo   User: %DB_USER%
echo.
echo You can now run 'mvn flyway:migrate' to apply database migrations.
echo Or start the Spring Boot application to automatically run migrations.

pause
