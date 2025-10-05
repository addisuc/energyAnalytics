#!/bin/bash

# AWS EC2 Deployment Script for EnergyFlow Analytics

set -e

echo "🚀 Deploying EnergyFlow Analytics to AWS EC2..."

# Build frontend
echo "📦 Building Angular frontend..."
cd frontend
npm install
npm run build
cd ..

# Build backend
echo "🔨 Building Spring Boot backend..."
mvn clean package -DskipTests

# Set environment variables for production
export SPRING_PROFILES_ACTIVE=prod
export SECURITY_ENABLED=true
export DATA_INITIALIZE=true

# Generate secure JWT secret if not provided
if [ -z "$JWT_SECRET" ]; then
    export JWT_SECRET=$(openssl rand -base64 64)
    echo "🔐 Generated JWT secret"
fi

# Use provided database password or generate one
if [ -z "$DB_PASSWORD" ]; then
    export DB_PASSWORD=$(openssl rand -base64 32)
    echo "🔐 Generated database password"
fi

# Deploy with Docker Compose
echo "🐳 Starting services with Docker Compose..."
docker-compose -f docker-compose.prod.yml down
docker-compose -f docker-compose.prod.yml up -d --build

echo "⏳ Waiting for services to start..."
sleep 30

# Check if services are running
echo "🔍 Checking service health..."
docker-compose -f docker-compose.prod.yml ps

# Test API endpoint
echo "🧪 Testing API health..."
curl -f http://localhost/api/weather/health || echo "⚠️  API not ready yet"

echo "✅ Deployment complete!"
echo "🌐 Access your application at: http://$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4 2>/dev/null || echo 'your-ec2-ip')"
echo "📊 Database: TimescaleDB with time series optimization"
echo "🔐 JWT Secret: $JWT_SECRET"
echo "🔑 DB Password: $DB_PASSWORD"