#!/bin/bash

# Execute build command
mvn clean install -DskipITs

# Check if the build was successful
if [ $? -eq 0 ]; then
    echo "Build successful, starting the application..."
    java -jar target/IceBreakerBackend-1.0.jar
else
    echo "Build failed, not starting the application."
fi
