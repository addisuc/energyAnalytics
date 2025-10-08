#!/bin/bash
yum update -y
yum install -y docker

# Install Docker Compose
curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose

# Start Docker
systemctl start docker
systemctl enable docker
usermod -a -G docker ec2-user

# Create app directory
mkdir -p /home/ec2-user/app
chown ec2-user:ec2-user /home/ec2-user/app
cd /home/ec2-user/app

echo "Infrastructure ready for application deployment"

# Create production Docker Compose file
cat > /home/ec2-user/app/docker-compose.prod.yml << 'EOF'
version: '3.8'
services:
  timescaledb:
    image: timescale/timescaledb:latest-pg15
    container_name: weather-timescaledb
    environment:
      POSTGRES_DB: weatherdb
      POSTGRES_USER: weather_user
      POSTGRES_PASSWORD: weather_pass_prod
    volumes:
      - timescale_data:/var/lib/postgresql/data
    networks:
      - weather-network
    restart: unless-stopped

  redis:
    image: redis:7-alpine
    container_name: weather-redis
    volumes:
      - redis_data:/data
    networks:
      - weather-network
    restart: unless-stopped

  weather-app:
    build: .
    container_name: weather-app
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SPRING_DATASOURCE_URL=jdbc:postgresql://timescaledb:5432/weatherdb
      - SPRING_DATASOURCE_USERNAME=weather_user
      - SPRING_DATASOURCE_PASSWORD=weather_pass_prod
      - SPRING_REDIS_HOST=redis
      - OPENWEATHER_API_KEY=${openweather_api_key}
    depends_on:
      - timescaledb
      - redis
    networks:
      - weather-network
    restart: unless-stopped

  nginx:
    image: nginx:alpine
    container_name: weather-nginx
    ports:
      - "80:80"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf
      - ./frontend/dist:/usr/share/nginx/html
    depends_on:
      - weather-app
    networks:
      - weather-network
    restart: unless-stopped

volumes:
  timescale_data:
  redis_data:

networks:
  weather-network:
    driver: bridge
EOF

# Create simple deployment script that starts pre-built containers
cat > /home/ec2-user/app/deploy.sh << 'EOF'
#!/bin/bash
set -e
cd /home/ec2-user/app

echo "🚀 Starting EnergyFlow Analytics..."

# Set environment variables
echo "export OPENWEATHER_API_KEY=${openweather_api_key}" > .env
echo "export DB_PASSWORD=weather_pass_prod" >> .env
echo "export SPRING_PROFILES_ACTIVE=prod" >> .env
source .env

# Start services with Docker Compose
echo "🐳 Starting all services..."
docker-compose -f docker-compose.prod.yml up -d

# Wait for services to be ready
echo "⏳ Waiting for services to start..."
sleep 60

# Check service health
echo "🔍 Checking service health..."
docker-compose -f docker-compose.prod.yml ps

echo "✅ EnergyFlow Analytics deployed successfully!"
echo "🌐 Application URL: http://$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4)"
echo "📊 TimescaleDB ready with time series optimization"
EOF

chmod +x /home/ec2-user/app/deploy.sh
chown -R ec2-user:ec2-user /home/ec2-user/app

# Run deployment as ec2-user
su - ec2-user -c "cd /home/ec2-user/app && ./deploy.sh" > /var/log/deployment.log 2>&1

echo "Full deployment complete" > /home/ec2-user/deployment-complete.txt