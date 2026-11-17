@echo off
REM Compile the project with javac on Windows (no Maven needed).
REM Needs: JDK 17+ and mysql-connector-j-8.0.33.jar in lib\ (see README step 2).
cd /d %~dp0
if not exist out mkdir out
if not exist lib mkdir lib
if not exist lib\mysql-connector-j-8.0.33.jar (
  echo NOTE: lib\mysql-connector-j-8.0.33.jar not found.
  echo Download it from https://dev.mysql.com/downloads/connector/j/ and place it in lib\,
  echo or run: mvn compile
  dir /s /b src\main\java\*.java > sources.txt
  javac -d out @sources.txt
) else (
  dir /s /b src\main\java\*.java > sources.txt
  javac -cp "lib\mysql-connector-j-8.0.33.jar" -d out @sources.txt
)
copy /y src\main\resources\db.properties out\ >nul
echo Compiled successfully. Classes are in out\
