@echo off
echo ================================
echo Credix - Finance Management
echo ================================
echo.
echo Cleaning and starting application...
echo.

REM Set MAVEN_HOME if not already set
if "%MAVEN_HOME%"=="" (
    if exist "C:\Program Files\Maven" (
        set "MAVEN_HOME=C:\Program Files\Maven"
    ) else if exist "C:\apache-maven" (
        set "MAVEN_HOME=C:\apache-maven"
    )
)

REM Add Maven to PATH
if not "%MAVEN_HOME%"=="" (
    set "PATH=%MAVEN_HOME%\bin;%PATH%"
)

call mvn clean compile javafx:run

pause
