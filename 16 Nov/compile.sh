#!/bin/bash
# Compile the project with javac (no Maven needed).
# Usage:  ./compile.sh
# Needs: JDK 17+ and mysql-connector-j-8.0.33.jar in lib/ (see README step 2).
set -e
cd "$(dirname "$0")"
mkdir -p out lib
if [ ! -f lib/mysql-connector-j-8.0.33.jar ]; then
  echo "NOTE: lib/mysql-connector-j-8.0.33.jar not found."
  echo "Download it from https://dev.mysql.com/downloads/connector/j/ and place it in lib/,"
  echo "or run: mvn compile (Maven downloads it automatically)."
  echo "Compiling WITHOUT the driver jar - DBConnection will still compile"
  echo "(it only references java.sql classes), but the app cannot connect at runtime."
  find src/main/java -name "*.java" > sources.txt
  javac -d out @sources.txt
else
  find src/main/java -name "*.java" > sources.txt
  javac -cp "lib/mysql-connector-j-8.0.33.jar" -d out @sources.txt
fi
cp src/main/resources/db.properties out/
echo "Compiled successfully. Classes are in out/"
