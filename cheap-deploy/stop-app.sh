#!/bin/bash

echo "🛑 Stopping EnergyFlow Application on EC2..."

# Get backend IP from Terraform output
BACKEND_IP=$(terraform output -raw backend_url | sed 's/http:\/\/\([^:]*\):.*/\1/')

if [ -z "$BACKEND_IP" ]; then
    echo "❌ Could not get backend IP. Make sure infrastructure is deployed."
    exit 1
fi

# Stop application on EC2
ssh -i ~/.ssh/id_rsa -o StrictHostKeyChecking=no ec2-user@$BACKEND_IP << 'EOF'
echo "Stopping EnergyFlow application..."

# Stop Spring Boot app using PID file
if [ -f app.pid ]; then
    PID=$(cat app.pid)
    if ps -p $PID > /dev/null 2>&1; then
        echo "Stopping application (PID: $PID)"
        kill $PID
        sleep 5
        # Force kill if still running
        if ps -p $PID > /dev/null 2>&1; then
            kill -9 $PID
        fi
    fi
    rm -f app.pid
fi

# Kill any remaining Java processes
pkill -f "weather-service" || true
pkill -f "java.*jar" || true

# Stop Docker containers
sudo docker stop postgres redis 2>/dev/null || true

echo "✅ Application stopped successfully"
EOF

echo "✅ EnergyFlow application stopped on EC2"