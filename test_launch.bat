@echo off
setlocal enabledelayedexpansion
cd /d "%~dp0"
title BANG! Test Launcher

echo ===========================
echo   BANG!  -  Test Launcher
echo ===========================
echo.

:: ── number of players ──────────────────────────────────────────────────────
:ask_n
set /p N=Number of players (2-7):
if not defined N goto ask_n
if %N% LSS 2 ( echo  Min 2 players. & goto ask_n )
if %N% GTR 7 ( echo  Max 7 players. & goto ask_n )

:: ── player names ───────────────────────────────────────────────────────────
echo.
for /l %%i in (1,1,%N%) do (
    set /p NAME%%i=  Player %%i name [default: P%%i]:
    if "!NAME%%i!"=="" set NAME%%i=P%%i
)

:: ── compile ────────────────────────────────────────────────────────────────
echo.
echo [1/3] Compiling...

set CP=project\lib\lanterna-3.1.2.jar
if not exist project\bin mkdir project\bin

:: write quoted source paths (skip bin output folder)
if exist .sources.txt del .sources.txt
for /r "project" %%f in (*.java) do (
    set src=%%f
    if "!src:bin=!" == "!src!" echo "%%f" >> .sources.txt
)

javac -cp "%CP%" -d "project\bin" @.sources.txt
del .sources.txt

if errorlevel 1 (
    echo.
    echo [FAIL] Compilation failed. Fix errors and try again.
    pause
    exit /b 1
)
echo [OK]  Compiled successfully.

:: ── launch ─────────────────────────────────────────────────────────────────
echo.
echo [2/3] Launching windows...

:: Player 1 = host (starts embedded server + first client)
echo  Starting !NAME1! (HOST)...
start "BANG![!NAME1!]" javaw -cp "project\bin;%CP%" Main "!NAME1!" %N%
timeout /t 1 /nobreak >nul

:: Players 2..N = clients
for /l %%i in (2,1,%N%) do (
    echo  Starting !NAME%%i!...
    start "BANG![!NAME%%i!]" javaw -cp "project\bin;%CP%" ClientMain localhost 12345 "!NAME%%i!"
    timeout /t 1 /nobreak >nul
)

:: ── done ───────────────────────────────────────────────────────────────────
echo.
echo [3/3] Done.  %N% windows opened.
echo.
echo  Switch between players using Alt+Tab or the taskbar.
echo  Each window title shows the player name (e.g. "BANG![Alice]").
echo.
pause
