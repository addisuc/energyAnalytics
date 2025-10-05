#!/bin/bash

set -e

echo "🚀 EnergyFlow Analytics - Single Step Deployment"
echo "==============================================="

# Validate prerequisites
echo "🔍 Validating prerequisites..."

command -v terraform >/dev/null 2>&1 || { echo "❌ Install Terraform: https://terraform.io/downloads"; exit 1; }

if [ ! -f "terraform.tfvars" ]; then
    echo "❌ Copy terraform.tfvars.example to terraform.tfvars and edit"
    exit 1
fi

echo "✅ Prerequisites validated"

# Clear any existing AWS credentials
unset AWS_ACCESS_KEY_ID
unset AWS_SECRET_ACCESS_KEY
unset AWS_DEFAULT_REGION
unset AWS_PROFILE
unset AWS_SESSION_TOKEN

# Prompt for AWS credentials
echo "🔐 AWS Credentials Required"
read -p "AWS Access Key ID: " AWS_ACCESS_KEY_ID
read -s -p "AWS Secret Access Key: " AWS_SECRET_ACCESS_KEY
echo
read -p "AWS Region (default: us-east-1): " AWS_REGION
AWS_REGION=${AWS_REGION:-us-east-1}

# Export ONLY the prompted credentials
export AWS_ACCESS_KEY_ID="$AWS_ACCESS_KEY_ID"
export AWS_SECRET_ACCESS_KEY="$AWS_SECRET_ACCESS_KEY"
export AWS_DEFAULT_REGION="$AWS_REGION"

# Disable AWS CLI profile usage
export AWS_PROFILE=""

echo "✅ AWS credentials configured for region: $AWS_REGION"

# Build application locally first
echo "📦 Building application locally..."
cd ..

# Build backend
echo "🔨 Building Spring Boot backend..."
mvn clean package -DskipTests

# Build frontend
echo "🎨 Building Angular frontend..."
cd frontend
npm install
npm run build
cd ..

# Return to terraform directory
cd cheap-deploy

echo "✅ Application built successfully"

# Deploy infrastructure and upload built files
echo "🏗️  Deploying infrastructure and application..."
terraform init -upgrade
terraform apply -auto-approve

# Wait for deployment to complete
echo "⏳ Waiting for application to be ready..."
sleep 180

# Show deployment results
echo ""
echo "🎉 Deployment Complete!"
echo "======================"
terraform output

echo ""
echo "💰 Monthly Cost: ~$31"
echo "⏱️  Application ready in ~3 minutes"
echo "🛑 Cleanup: ./cleanup.sh"