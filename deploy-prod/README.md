# EnergyFlow Production Deployment

## Architecture
- **Frontend**: S3 Static Website with CloudFront (optional)
- **Backend**: ECS Fargate with Application Load Balancer
- **Database**: RDS PostgreSQL (separate, managed)
- **Container Registry**: ECR
- **Monitoring**: CloudWatch
- **Estimated Cost**: ~$25-40/month

## Usage

### Deploy
```bash
cd deploy-prod
bash deploy.sh
```

### Cleanup
```bash
bash cleanup.sh
```

## Cost Breakdown (Estimated)
- ECS Fargate: $15-20/month
- RDS PostgreSQL (db.t3.micro): $12-15/month
- Application Load Balancer: $16/month
- S3 + CloudWatch: $2-3/month
- **Total**: ~$45-54/month

## Features
- ✅ Production-grade ECS Fargate deployment
- ✅ Separate managed RDS PostgreSQL
- ✅ Application Load Balancer with health checks
- ✅ Multi-AZ deployment for high availability
- ✅ Container orchestration with ECS
- ✅ Centralized logging with CloudWatch
- ✅ ECR for container image management
- ✅ VPC with public/private subnets
- ✅ Security groups with least privilege
- ✅ Automated backups and maintenance windows

## Infrastructure Components

### Networking
- VPC with public and private subnets across 2 AZs
- Internet Gateway for public access
- Security groups for ALB, ECS, and RDS

### Compute
- ECS Fargate cluster for containerized applications
- Application Load Balancer for traffic distribution
- Auto-scaling capabilities (configurable)

### Database
- RDS PostgreSQL in private subnets
- Automated backups with 7-day retention
- Encryption at rest enabled
- Multi-AZ deployment ready

### Storage & Registry
- S3 bucket for frontend static files
- ECR repository for Docker images
- CloudWatch for logs and monitoring

## Security Features
- Database in private subnets (no internet access)
- Security groups with minimal required access
- Encrypted storage for RDS
- IAM roles with least privilege principles
- Container image vulnerability scanning

## Deployment Process
1. Infrastructure provisioning with Terraform
2. Docker image build and push to ECR
3. ECS service deployment
4. Frontend upload to S3
5. DNS and SSL configuration (manual)

## Monitoring & Maintenance
- CloudWatch logs for application monitoring
- RDS automated backups
- Container health checks
- Infrastructure as Code for reproducibility