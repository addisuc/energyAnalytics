#!/bin/bash

# Angular Weather Service Build and Deploy Script

echo "🚀 Building Angular Weather Service..."

# Install dependencies if needed
if [ ! -d "node_modules" ]; then
    echo "📦 Installing dependencies..."
    npm install
fi

# Build for production
echo "🔨 Building for production..."
npm run build

if [ $? -ne 0 ]; then
    echo "❌ Build failed!"
    exit 1
fi

echo "✅ Build successful!"

# Deploy to S3
echo "🌐 Deploying to S3..."
aws s3 sync dist/frontend/ s3://weather-frontend-cr2e0cs5/ --delete

if [ $? -eq 0 ]; then
    echo "✅ Deployment successful!"
    echo "🌐 URL: http://weather-frontend-cr2e0cs5.s3-website-us-east-1.amazonaws.com/"
else
    echo "❌ Deployment failed. Check AWS credentials and permissions."
    exit 1
fi