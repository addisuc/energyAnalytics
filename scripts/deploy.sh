#!/bin/bash

# Energy Analytics Platform - Deployment Script

set -e

ENVIRONMENT=${1:-staging}
VERSION=${2:-latest}

echo "Deploying Energy Analytics Platform"
echo "Environment: $ENVIRONMENT"
echo "Version: $VERSION"

# Load environment-specific configuration
if [ -f ".env.$ENVIRONMENT" ]; then
    source ".env.$ENVIRONMENT"
    echo "Loaded environment configuration for $ENVIRONMENT"
else
    echo "Warning: No environment file found for $ENVIRONMENT"
fi

# Pre-deployment checks
echo "Running pre-deployment checks..."

# Check if required environment variables are set
required_vars=("DB_PASSWORD" "JWT_SECRET" "OPENWEATHER_API_KEY")
for var in "${required_vars[@]}"; do
    if [ -z "${!var}" ]; then
        echo "Error: Required environment variable $var is not set"
        exit 1
    fi
done

# Check if services are healthy
echo "Checking service health..."
if ! docker-compose -f docker-compose.prod.yml ps | grep -q "Up"; then
    echo "Warning: Some services may not be running"
fi

# Backup database before deployment
echo "Creating backup before deployment..."
./scripts/backup.sh

# Pull latest images
echo "Pulling latest Docker images..."
docker-compose -f docker-compose.prod.yml pull

# Deploy with zero-downtime strategy
echo "Starting deployment..."

# Start new containers
docker-compose -f docker-compose.prod.yml up -d --remove-orphans

# Wait for health checks
echo "Waiting for services to be healthy..."
sleep 30

# Verify deployment
echo "Verifying deployment..."
if curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo "✅ Backend health check passed"
else
    echo "❌ Backend health check failed"
    exit 1
fi

if curl -f http://localhost:80 > /dev/null 2>&1; then
    echo "✅ Frontend health check passed"
else
    echo "❌ Frontend health check failed"
    exit 1
fi

# Clean up old images
echo "Cleaning up old Docker images..."
docker image prune -f

echo "🚀 Deployment completed successfully!"
echo "Application is available at: http://localhost"