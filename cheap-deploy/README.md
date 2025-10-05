# EnergyFlow Analytics - AWS Deployment

Fully automated Terraform deployment for EnergyFlow Analytics with TimescaleDB.

## Quick Start

1. **Setup**:
   ```bash
   cp terraform.tfvars.example terraform.tfvars
   # Edit terraform.tfvars with your OpenWeather API key
   ```

2. **Deploy**:
   ```bash
   terraform init
   terraform apply
   ```

3. **Access**: Use the output URL to access your application

4. **Cleanup**:
   ```bash
   ./cleanup.sh
   ```

## What Gets Deployed

- **EC2 t3.medium** with TimescaleDB + Redis + Application + Nginx
- **Elastic IP** for stable access
- **Security Groups** with proper ports
- **Fully automated** build and deployment

## Monthly Cost: ~$35

## Architecture

```
Internet → Elastic IP → EC2 Instance
├── Nginx (Port 80) → Angular UI
├── Spring Boot (Port 8080) → API
├── TimescaleDB (Port 5432) → Time Series DB
└── Redis (Port 6379) → Cache
```