#!/bin/bash
# Run the console application.
# Usage:  ./run.sh   (compile first with ./compile.sh or mvn compile)
set -e
cd "$(dirname "$0")"
if [ ! -d out ]; then
  echo "out/ not found - running ./compile.sh first..."
  ./compile.sh
fi
if [ -f lib/mysql-connector-j-8.0.33.jar ]; then
  java -cp "out:lib/mysql-connector-j-8.0.33.jar" com.recruitment.app.RecruitmentApp
else
  echo "WARNING: MySQL driver jar not found in lib/ - the app will start but cannot connect."
  java -cp "out" com.recruitment.app.RecruitmentApp
fi
