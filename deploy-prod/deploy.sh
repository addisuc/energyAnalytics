#!/bin/bash

set -e

echo "🚀 Starting EnergyFlow Production Deployment..."

# Check if required tools are installed
command -v terraform >/dev/null 2>&1 || { echo "❌ Terraform is required but not installed. Aborting." >&2; exit 1; }
command -v aws >/dev/null 2>&1 || { echo "❌ AWS CLI is required but not installed. Aborting." >&2; exit 1; }
command -v docker >/dev/null 2>&1 || { echo "❌ Docker is required but not installed. Aborting." >&2; exit 1; }

# Check AWS credentials
aws sts get-caller-identity >/dev/null 2>&1 || { echo "❌ AWS credentials not configured. Run 'aws configure' first." >&2; exit 1; }

# Get user inputs
read -p "Enter AWS region (default: us-east-1): " AWS_REGION
AWS_REGION=${AWS_REGION:-us-east-1}

read -s -p "Enter database password: " DB_PASSWORD
echo
read -s -p "Enter OpenWeatherMap API key: " OPENWEATHER_API_KEY
echo

# Export variables
export TF_VAR_aws_region=$AWS_REGION
export TF_VAR_db_password=$DB_PASSWORD
export TF_VAR_openweather_api_key=$OPENWEATHER_API_KEY

echo "📋 Deployment Configuration:"
echo "  - Region: $AWS_REGION"
echo "  - Environment: Production"
echo "  - Architecture: ECS Fargate + RDS PostgreSQL"

# Initialize Terraform
echo "🔧 Initializing Terraform..."
terraform init

# Plan deployment
echo "📊 Planning deployment..."
terraform plan

# Confirm deployment
read -p "Do you want to proceed with deployment? (y/N): " CONFIRM
if [[ $CONFIRM != [yY] ]]; then
    echo "❌ Deployment cancelled."
    exit 1
fi

# Apply Terraform
echo "🚀 Deploying infrastructure..."
terraform apply -auto-approve

# Get outputs
echo "📋 Deployment completed! Getting outputs..."
ECR_URL=$(terraform output -raw ecr_repository_url)
ALB_DNS=$(terraform output -raw load_balancer_dns)
FRONTEND_URL=$(terraform output -raw frontend_website_url)
FRONTEND_BUCKET=$(terraform output -raw frontend_bucket_name)

echo "✅ Production deployment completed successfully!"
echo ""
echo "📋 Deployment Information:"
echo "  - ECR Repository: $ECR_URL"
echo "  - Application URL: http://$ALB_DNS"
echo "  - Frontend URL: http://$FRONTEND_URL"
echo ""
echo "🔧 Next Steps - Deploying Application:"
echo "  1. Building and pushing Docker image to ECR..."

# Build and push Docker image
echo "📦 Building Docker image..."
cd ..
docker build -t energyflow-prod .
cd deploy-prod

# Login to ECR and push
echo "🔐 Logging into ECR..."
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $ECR_URL

echo "🏷️  Tagging and pushing image..."
docker tag energyflow-prod:latest $ECR_URL:latest
docker push $ECR_URL:latest

# Update ECS service to use new image
echo "🔄 Updating ECS service..."
aws ecs update-service \
  --cluster ${var.project_name}-cluster \
  --service ${var.project_name}-service \
  --force-new-deployment \
  --region $AWS_REGION

# Upload frontend to S3
echo "🌐 Deploying frontend to S3..."
cd ../frontend
npm install
npm run build
cd ../deploy-prod

# Update frontend API URL and upload
if [ -d "../frontend/dist/frontend" ]; then
    if ls ../frontend/dist/frontend/main*.js 1> /dev/null 2>&1; then
        sed -i.bak "s|http://localhost:8080|http://$ALB_DNS|g" ../frontend/dist/frontend/main*.js
    fi
    aws s3 sync ../frontend/dist/frontend/ s3://$FRONTEND_BUCKET/ --delete
else
    echo "⚠️  Frontend build not found, uploading basic index.html"
    echo "<html><body><h1>EnergyFlow Frontend</h1><p>Backend API: <a href='http://$ALB_DNS'>http://$ALB_DNS</a></p></body></html>" > temp_index.html
    aws s3 cp temp_index.html s3://$FRONTEND_BUCKET/index.html
    rm temp_index.html
fi
echo ""
echo "💡 To clean up resources, run: ./cleanup.sh"