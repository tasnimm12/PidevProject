@echo off
echo ================================
echo Finance Management System Setup
echo ================================
echo.
echo This script will help you set up the database.
echo.
echo Step 1: Make sure MySQL is running on localhost:3306
echo Step 2: Make sure you have created the 'finance1' database
echo.
echo To create the database, open MySQL and run:
echo    CREATE DATABASE finance1;
echo.
echo Then import the schema:
echo    mysql -u root -p finance1 ^< database/finance1.sql
echo.
echo (Optional) Import sample data:
echo    mysql -u root -p finance1 ^< database/sample_data.sql
echo.
echo ================================
echo.
echo After setup, run the application using: run.bat
echo.
pause
