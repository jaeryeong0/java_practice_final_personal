@echo off
setlocal
cd /d "%~dp0"

set MAIN_CLASS=TestMain
set JAR_NAME=TestMain.jar
set LIB=lib\lanterna-3.1.2.jar
set OUT=out

echo [1/4] Cleaning output directory...
if exist %OUT% rmdir /s /q %OUT%
mkdir %OUT%

echo [2/4] Compiling Java sources...
for /r . %%f in (*.java) do (
    echo   %%f
)
javac -cp "%LIB%" -d %OUT% -sourcepath . ^
    TestMain.java ^
    TestAssets.java ^
    ClientMain.java ^
    Main.java ^
    ServerMain.java ^
    tools\Assets.java ^
    tools\UIPositions.java ^
    tools\Util.java ^
    scene\Scene.java ^
    scene\SceneManager.java ^
    scene\NicknameScene.java ^
    scene\TitleScene.java ^
    scene\WaitingRoomScene.java ^
    scene\GamePlayScene.java ^
    net\BangClient.java ^
    net\BangServer.java ^
    net\ClientGameState.java ^
    net\Protocol.java ^
    game\GameLogic.java

if errorlevel 1 (
    echo [ERROR] Compilation failed.
    exit /b 1
)

echo [3/4] Extracting lanterna into out\...
cd %OUT%
jar xf ..\%LIB%
cd ..

echo [4/4] Packaging fat JAR: %JAR_NAME%...
jar cfe %JAR_NAME% %MAIN_CLASS% -C %OUT% .

if errorlevel 1 (
    echo [ERROR] JAR creation failed.
    exit /b 1
)

echo.
echo Done! Run with:
echo   java -jar %JAR_NAME%
echo   java -jar %JAR_NAME% Alice Bob Carol
endlocal
