@echo off
REM Run the console application on Windows.
REM Usage: run.bat  (compile first with compile.bat or mvn compile)
cd /d %~dp0
if not exist out (
  echo out\ not found - running compile.bat first...
  call compile.bat
)
if exist lib\mysql-connector-j-8.0.33.jar (
  java -cp "out;lib\mysql-connector-j-8.0.33.jar" com.recruitment.app.RecruitmentApp
) else (
  echo WARNING: MySQL driver jar not found in lib\ - the app will start but cannot connect.
  java -cp "out" com.recruitment.app.RecruitmentApp
)
