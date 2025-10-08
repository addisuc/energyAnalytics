#!/bin/bash

set -e

echo "🧹 EnergyFlow Analytics - AWS Cleanup"
echo "====================================="

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

echo "✅ AWS credentials configured"

# Confirm destruction
read -p "⚠️  This will destroy ALL AWS resources. Are you sure? (yes/no): " confirm
if [ "$confirm" != "yes" ]; then
    echo "❌ Cleanup cancelled"
    exit 1
fi

# Show what will be destroyed
echo "📋 Resources to be destroyed:"
terraform plan -destroy

read -p "Continue with destruction? (yes/no): " final_confirm
if [ "$final_confirm" != "yes" ]; then
    echo "❌ Cleanup cancelled"
    exit 1
fi

# Destroy all Terraform resources
echo "💥 Destroying all infrastructure..."
terraform destroy -auto-approve

# Clean up any remaining key pairs manually
echo "🔑 Cleaning up SSH key pairs..."
aws ec2 describe-key-pairs --query 'KeyPairs[?starts_with(KeyName, `energyflow-dev-key`)].KeyName' --output text | xargs -r -n1 aws ec2 delete-key-pair --key-name 2>/dev/null || true

# Clean up local Terraform state
echo "🗑️  Cleaning up local files..."
rm -f terraform.tfstate*
rm -f .terraform.lock.hcl
rm -rf .terraform/

echo ""
echo "✅ Cleanup Complete!"
echo "==================="
echo "💰 All AWS resources destroyed - no more charges!"
echo "📊 Estimated monthly savings: ~$35"
echo "🎯 Ready for next deployment when needed"