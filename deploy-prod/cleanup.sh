#!/bin/bash

set -e

echo "🧹 Starting EnergyFlow Production Cleanup..."

# Check if Terraform is installed
command -v terraform >/dev/null 2>&1 || { echo "❌ Terraform is required but not installed. Aborting." >&2; exit 1; }

# Check if terraform state exists
if [ ! -f "terraform.tfstate" ]; then
    echo "❌ No Terraform state found. Nothing to clean up."
    exit 1
fi

echo "⚠️  WARNING: This will destroy production resources."
echo ""
echo "Choose cleanup option:"
echo "  1. App Only - Keep database and infrastructure (recommended)"
echo "  2. Everything - Destroy ALL including database (DATA LOSS!)"
echo ""
read -p "Enter choice (1 or 2): " CLEANUP_CHOICE

if [[ $CLEANUP_CHOICE == "1" ]]; then
    echo "🗑️  App-only cleanup: Stopping ECS service and clearing S3..."
    
    # Stop ECS service
    aws ecs update-service \
        --cluster energyflow-prod-cluster \
        --service energyflow-prod-service \
        --desired-count 0 \
        --region $(terraform output -raw aws_region) 2>/dev/null || true
    
    # Clear S3 bucket
    BUCKET_NAME=$(terraform output -raw frontend_bucket_name 2>/dev/null)
    if [ ! -z "$BUCKET_NAME" ]; then
        aws s3 rm s3://$BUCKET_NAME --recursive 2>/dev/null || true
    fi
    
    echo "✅ App cleanup completed! Database and infrastructure preserved."
    echo "📊 Database data is safe and will be available for next deployment."
    exit 0
    
elif [[ $CLEANUP_CHOICE == "2" ]]; then
    echo "⚠️  FULL CLEANUP: This will destroy ALL resources including database!"
    echo "  - ECS Cluster and Services"
    echo "  - RDS PostgreSQL Database (ALL DATA WILL BE LOST)"
    echo "  - Load Balancer"
    echo "  - VPC and Networking"
    echo "  - S3 Frontend Bucket"
    echo "  - ECR Repository"
    echo "  - CloudWatch Logs"
    echo ""
    
    read -p "Are you absolutely sure? Type 'DELETE' to confirm: " CONFIRM
    if [[ $CONFIRM != "DELETE" ]]; then
        echo "❌ Cleanup cancelled."
        exit 1
    fi
else
    echo "❌ Invalid choice. Cleanup cancelled."
    exit 1
fi

echo "🔧 Initializing Terraform..."
terraform init

echo "📊 Planning destruction..."
terraform plan -destroy

read -p "Proceed with destruction? (y/N): " FINAL_CONFIRM
if [[ $FINAL_CONFIRM != [yY] ]]; then
    echo "❌ Cleanup cancelled."
    exit 1
fi

echo "🧹 Destroying infrastructure..."
terraform destroy -auto-approve

echo "🧹 Cleaning up local files..."
rm -f terraform.tfstate*
rm -f .terraform.lock.hcl
rm -rf .terraform/

echo "✅ Production cleanup completed successfully!"
echo ""
echo "📋 All resources have been destroyed:"
echo "  ✅ ECS Cluster and Services"
echo "  ✅ RDS PostgreSQL Database"
echo "  ✅ Load Balancer"
echo "  ✅ VPC and Networking"
echo "  ✅ S3 Frontend Bucket"
echo "  ✅ ECR Repository"
echo "  ✅ CloudWatch Logs"
echo "  ✅ Local Terraform state"
echo ""
echo "💡 All AWS resources have been released and billing has stopped."