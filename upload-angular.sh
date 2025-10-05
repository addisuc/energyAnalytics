#!/bin/bash

echo "Uploading Angular files to S3..."

# Copy files to EC2
scp -i ~/.ssh/id_rsa -r frontend/dist/frontend/browser/* ec2-user@54.196.61.107:/tmp/angular-deploy/

# Upload to S3 from EC2 (using existing credentials from terraform)
ssh -i ~/.ssh/id_rsa ec2-user@54.196.61.107 "
cd /home/ec2-user/weather-app
export AWS_ACCESS_KEY_ID=\$(grep access_key terraform.tfvars | cut -d'=' -f2 | tr -d '\" ')
export AWS_SECRET_ACCESS_KEY=\$(grep secret_key terraform.tfvars | cut -d'=' -f2 | tr -d '\" ')
export AWS_DEFAULT_REGION=us-east-1
aws s3 sync /tmp/angular-deploy/ s3://weather-frontend-cr2e0cs5/ --delete
"

echo "✅ Angular app deployed!"
echo "🌐 URL: http://weather-frontend-cr2e0cs5.s3-website-us-east-1.amazonaws.com/"