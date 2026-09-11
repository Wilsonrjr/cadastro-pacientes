@echo off
cd /d "%~dp0"

if not exist out mkdir out
dir /s /b src\main\java\*.java > out\fontes.txt

javac -encoding UTF-8 -d out @out\fontes.txt
if errorlevel 1 (
    echo.
    echo Falha ao compilar.
    pause
    exit /b 1
)

java -cp out com.clinica.ClinicaAplicacao
pause
