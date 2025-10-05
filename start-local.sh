#!/bin/bash

echo "Starting Weather Service locally..."

# Kill any existing processes on port 8080
echo "Checking for existing processes on port 8080..."
lsof -ti:8080 | xargs kill -9 2>/dev/null || true

# Wait a moment for cleanup
sleep 2

# Start the Spring Boot application with local profile
echo "Starting Spring Boot application with local profile..."
export SPRING_PROFILES_ACTIVE=local
mvn clean spring-boot:run -Dspring-boot.run.profiles=local

echo "Application should be available at http://localhost:8080"
echo "Health check: http://localhost:8080/api/health"
echo "H2 Console: http://localhost:8080/h2-console"