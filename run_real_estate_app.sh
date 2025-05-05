#!/bin/bash

# Set your classpath to include the PostgreSQL JDBC driver
# Make sure you update the path to the JDBC jar if needed
JDBC_JAR="postgresql-42.7.3.jar"

# Compile all Java files
echo "Compiling Java files..."
javac -cp ".:$JDBC_JAR" *.java

if [ $? -ne 0 ]; then
    echo "Compilation failed."
    exit 1
fi

# Run the application
echo "Running the Real Estate CLI application..."
java -cp ".:$JDBC_JAR" Main
