# AWS Fargate Deployment Guide

## Architecture Overview

```
Internet → ALB → ECS Fargate (Frontend + Backend) → RDS PostgreSQL + ElastiCache Redis
```

## Prerequisites

1. **AWS CLI configured** with appropriate permissions
2. **Terraform installed** (v1.0+)
3. **Docker installed** and running
4. **AWS Account** with ECS, RDS, ElastiCache permissions

## Quick Deployment

```bash
# 1. Configure variables
cp terraform/terraform.tfvars.example terraform/terraform.tfvars
# Edit terraform.tfvars with your values

# 2. Deploy everything
./deploy.sh
```

## Manual Deployment Steps

### 1. Infrastructure Setup
```bash
cd terraform
terraform init
terraform plan
terraform apply
```

### 2. Build & Push Images
```bash
# Get ECR login
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <account-id>.dkr.ecr.us-east-1.amazonaws.com

# Build and push backend
docker build -t weather-backend .
docker tag weather-backend:latest <backend-ecr-url>:latest
docker push <backend-ecr-url>:latest

# Build and push frontend
cd frontend
docker build -t weather-frontend .
docker tag weather-frontend:latest <frontend-ecr-url>:latest
docker push <frontend-ecr-url>:latest
```

### 3. Update ECS Services
```bash
aws ecs update-service --cluster weather-cluster --service weather-backend --force-new-deployment
aws ecs update-service --cluster weather-cluster --service weather-frontend --force-new-deployment
```

## AWS Resources Created

- **VPC** with public/private subnets
- **ECS Fargate Cluster** with 2 services
- **RDS PostgreSQL** (db.t3.micro)
- **ElastiCache Redis** (cache.t3.micro)
- **Application Load Balancer**
- **ECR Repositories** for images
- **CloudWatch Log Groups**
- **Security Groups** with proper access

## Access Points

After deployment:
- **Frontend**: `http://<alb-dns-name>`
- **Backend API**: `http://<alb-dns-name>:8080`
- **Swagger Docs**: `http://<alb-dns-name>:8080/swagger-ui.html`
- **Health Check**: `http://<alb-dns-name>:8080/api/weather/health`

## Configuration

### Environment Variables (Auto-configured)
- `SPRING_DATASOURCE_URL`: RDS PostgreSQL endpoint
- `SPRING_REDIS_HOST`: ElastiCache Redis endpoint
- `OPENWEATHER_API_KEY`: Weather API key

### Scaling
- **Backend**: 2 Fargate tasks (512 CPU, 1024 MB)
- **Frontend**: 2 Fargate tasks (256 CPU, 512 MB)
- **Auto-scaling**: Configure in ECS service if needed

## Monitoring

- **CloudWatch Logs**: `/ecs/weather-backend`, `/ecs/weather-frontend`
- **ECS Console**: Monitor task health and performance
- **RDS Monitoring**: Database performance metrics
- **ALB Metrics**: Request count, latency, errors

## Security

- **Private Subnets**: Database and cache in private subnets
- **Security Groups**: Restrictive access between services
- **IAM Roles**: Minimal permissions for ECS tasks
- **VPC**: Isolated network environment

## Cost Optimization

- **Fargate**: Pay per task execution time
- **RDS**: db.t3.micro for development
- **ElastiCache**: cache.t3.micro for development
- **ALB**: Pay per hour + LCU usage

## Cleanup

```bash
cd terraform
terraform destroy
```

## Troubleshooting

1. **ECS Tasks failing**: Check CloudWatch logs
2. **Database connection**: Verify security groups
3. **Load balancer 503**: Check target group health
4. **Image pull errors**: Verify ECR permissions

## Production Considerations

- Enable **HTTPS** with ACM certificate
- Use **larger instance types** for production
- Enable **RDS backups** and **Multi-AZ**
- Set up **CloudWatch alarms**
- Configure **auto-scaling policies**