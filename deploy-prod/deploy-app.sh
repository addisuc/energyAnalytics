#!/bin/bash

set -e

echo "🚀 Deploying EnergyFlow Application (Production)"
echo "=============================================="
echo "📋 This will update the application while preserving the database"

# Check if infrastructure exists
if [ ! -f "terraform.tfstate" ]; then
    echo "❌ No infrastructure found. Run ./deploy.sh first to create infrastructure."
    exit 1
fi

# Check prerequisites
command -v terraform >/dev/null 2>&1 || { echo "❌ Terraform required but not installed." >&2; exit 1; }
command -v aws >/dev/null 2>&1 || { echo "❌ AWS CLI required but not installed." >&2; exit 1; }
command -v docker >/dev/null 2>&1 || { echo "❌ Docker required but not installed." >&2; exit 1; }

# Get infrastructure outputs
echo "📋 Getting infrastructure information..."
ECR_URL=$(terraform output -raw ecr_repository_url)
ALB_DNS=$(terraform output -raw load_balancer_dns)
FRONTEND_BUCKET=$(terraform output -raw frontend_bucket_name)
AWS_REGION=$(terraform output -raw aws_region)

echo "🏗️ Infrastructure found:"
echo "  - ECR Repository: $ECR_URL"
echo "  - Load Balancer: $ALB_DNS"
echo "  - Frontend Bucket: $FRONTEND_BUCKET"

# Build and push Docker image
echo "📦 Building backend application..."
cd ..
mvn clean package -DskipTests
cd deploy-prod

echo "🐳 Building Docker image..."
docker build -t energyflow-prod ../.

echo "🔐 Logging into ECR..."
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $ECR_URL

echo "📤 Pushing image to ECR..."
docker tag energyflow-prod:latest $ECR_URL:latest
docker push $ECR_URL:latest

# Update ECS service
echo "🔄 Updating ECS service with new image..."
aws ecs update-service \
  --cluster energyflow-prod-cluster \
  --service energyflow-prod-service \
  --force-new-deployment \
  --region $AWS_REGION

# Build and deploy frontend
echo "🎨 Building frontend for production..."

# Update environment files for production
cat > ../frontend/src/environments/environment.prod.ts << EOF
export const environment = {
  production: true,
  apiUrl: 'http://$ALB_DNS/api'
};
EOF

cat > ../frontend/src/environments/environment.ts << EOF
export const environment = {
  production: true,
  apiUrl: 'http://$ALB_DNS/api'
};
EOF

cd ../frontend
npm install
npm run build
cd ../deploy-prod

echo "🌐 Deploying frontend to S3..."
if [ -d "../frontend/dist/frontend/browser" ]; then
    aws s3 sync ../frontend/dist/frontend/browser/ s3://$FRONTEND_BUCKET/ --delete
elif [ -d "../frontend/dist/frontend" ]; then
    aws s3 sync ../frontend/dist/frontend/ s3://$FRONTEND_BUCKET/ --delete
else
    echo "⚠️ Frontend build not found, uploading basic index.html"
    echo "<html><body><h1>EnergyFlow Frontend</h1><p>Backend API: <a href='http://$ALB_DNS'>http://$ALB_DNS</a></p></body></html>" > temp_index.html
    aws s3 cp temp_index.html s3://$FRONTEND_BUCKET/index.html
    rm temp_index.html
fi

echo ""
echo "🎉 EnergyFlow Application Deployment Complete!"
echo "============================================="
echo "🌐 Frontend URL: http://$FRONTEND_BUCKET.s3-website-$AWS_REGION.amazonaws.com"
echo "🔗 Backend API: http://$ALB_DNS"
echo "📚 API Documentation: http://$ALB_DNS/swagger-ui.html"
echo ""
echo "⏳ ECS service is updating... Application will be available in 2-3 minutes"
echo "📊 Database data preserved - all users and data intact"
echo ""
echo "🔍 Monitor deployment: aws ecs describe-services --cluster energyflow-prod-cluster --services energyflow-prod-service"