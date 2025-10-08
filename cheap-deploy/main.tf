terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

# VPC
resource "aws_vpc" "main" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_hostnames = true
  enable_dns_support   = true
  
  tags = {
    Name = "energyflow-dev-vpc"
    Environment = "development"
  }
}

# Internet Gateway
resource "aws_internet_gateway" "main" {
  vpc_id = aws_vpc.main.id
  
  tags = {
    Name = "weather-igw"
  }
}

# Public Subnet
resource "aws_subnet" "public" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "10.0.1.0/24"
  availability_zone       = data.aws_availability_zones.available.names[0]
  map_public_ip_on_launch = true
  
  tags = {
    Name = "weather-public-subnet"
  }
}

# Route Table
resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id
  
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.main.id
  }
  
  tags = {
    Name = "weather-public-rt"
  }
}

resource "aws_route_table_association" "public" {
  subnet_id      = aws_subnet.public.id
  route_table_id = aws_route_table.public.id
}

# Security Group
resource "aws_security_group" "web" {
  name_prefix = "weather-"
  vpc_id      = aws_vpc.main.id
  
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  
  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  
  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
  
  tags = {
    Name = "weather-sg"
  }
}

# Key Pair with random suffix to avoid conflicts
resource "aws_key_pair" "main" {
  key_name   = "energyflow-dev-key-${random_string.key_suffix.result}"
  public_key = file("~/.ssh/id_rsa.pub")
}

resource "random_string" "key_suffix" {
  length  = 4
  special = false
  upper   = false
}

# Using dynamic public IP to save $3.65/month

# EC2 Instance (t3.medium for TimescaleDB)
resource "aws_instance" "backend" {
  ami           = data.aws_ami.amazon_linux.id
  instance_type = "t3.medium"
  key_name      = aws_key_pair.main.key_name
  subnet_id     = aws_subnet.public.id
  
  vpc_security_group_ids = [aws_security_group.web.id]
  
  user_data = base64encode(templatefile("${path.module}/user-data.sh", {
    openweather_api_key = var.openweather_api_key
  }))
  
  tags = {
    Name = "energyflow-dev-backend"
    Environment = "development"
  }
  
  # Wait for instance to be ready before provisioning
  provisioner "remote-exec" {
    inline = [
      "while [ ! -f /home/ec2-user/deployment-complete.txt ]; do sleep 10; done",
      "echo 'Instance ready for file upload'"
    ]
    
    connection {
      type        = "ssh"
      user        = "ec2-user"
      private_key = file("~/.ssh/id_rsa")
      host        = self.public_ip
    }
  }
  
  # Create and upload application archive
  provisioner "local-exec" {
    command = <<-EOT
      cd ..
      tar -czf /tmp/energyflow-app.tar.gz \
        docker-compose.prod.yml \
        nginx.conf \
        target/weather-service-1.0.0.jar \
        frontend/dist/frontend/browser
    EOT
  }
  
  provisioner "file" {
    source      = "/tmp/energyflow-app.tar.gz"
    destination = "/home/ec2-user/app.tar.gz"
    
    connection {
      type        = "ssh"
      user        = "ec2-user"
      private_key = file("~/.ssh/id_rsa")
      host        = self.public_ip
    }
  }
  
  provisioner "remote-exec" {
    inline = [
      "cd /home/ec2-user/app",
      "tar -xzf ../app.tar.gz --strip-components=0",
      "mv frontend/dist/frontend/browser frontend/",
      "rm -f ../app.tar.gz"
    ]
    
    connection {
      type        = "ssh"
      user        = "ec2-user"
      private_key = file("~/.ssh/id_rsa")
      host        = self.public_ip
    }
  }
  
  # Deploy application with proper environment and paths
  provisioner "remote-exec" {
    inline = [
      "cd /home/ec2-user/app",
      "chmod +x deploy.sh",
      "export OPENWEATHER_API_KEY=${var.openweather_api_key}",
      "./deploy.sh",
      "sleep 30",
      "export OPENWEATHER_API_KEY=${var.openweather_api_key}",
      "docker-compose -f docker-compose.prod.yml down",
      "docker-compose -f docker-compose.prod.yml up -d",
      "sleep 60",
      "docker exec timescaledb psql -U weather_user -d weatherdb -c \"CREATE EXTENSION IF NOT EXISTS timescaledb;\"",
      "docker exec timescaledb psql -U weather_user -d weatherdb -c \"CREATE TABLE IF NOT EXISTS users (id BIGSERIAL PRIMARY KEY, username VARCHAR(255) UNIQUE NOT NULL, email VARCHAR(255) UNIQUE NOT NULL, password VARCHAR(255) NOT NULL, role VARCHAR(50) DEFAULT 'USER', first_name VARCHAR(255), last_name VARCHAR(255), company_name VARCHAR(255), job_title VARCHAR(255), phone_number VARCHAR(255), subscription_plan VARCHAR(50) DEFAULT 'FREE');\"",
      "docker exec timescaledb psql -U weather_user -d weatherdb -c \"CREATE TABLE IF NOT EXISTS subscriptions (id BIGSERIAL PRIMARY KEY, user_id BIGINT REFERENCES users(id), plan VARCHAR(50) NOT NULL, status VARCHAR(50) NOT NULL, start_date TIMESTAMP, usage_limit INTEGER, current_usage INTEGER DEFAULT 0);\"",
      "docker exec timescaledb psql -U weather_user -d weatherdb -c \"INSERT INTO users (username, email, password, first_name, last_name, role, subscription_plan) VALUES ('demo', 'demo@weather.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye/Eo9hfBVV2AfVufF7mXSuHxspnsgTzu', 'Demo', 'User', 'USER', 'PRO') ON CONFLICT (email) DO NOTHING;\"",
      "docker exec timescaledb psql -U weather_user -d weatherdb -c \"INSERT INTO subscriptions (user_id, plan, status, start_date, usage_limit, current_usage) SELECT id, 'PRO', 'ACTIVE', NOW(), -1, 0 FROM users WHERE email = 'demo@weather.com' ON CONFLICT (user_id) DO NOTHING;\"",
      "docker-compose -f docker-compose.prod.yml restart weather-app",
      "sleep 30",
      "docker ps"
    ]
    
    connection {
      type        = "ssh"
      user        = "ec2-user"
      private_key = file("~/.ssh/id_rsa")
      host        = self.public_ip
    }
  }
}

resource "random_string" "bucket_suffix" {
  length  = 8
  special = false
  upper   = false
}

data "aws_availability_zones" "available" {
  state = "available"
}

data "aws_ami" "amazon_linux" {
  most_recent = true
  owners      = ["amazon"]
  
  filter {
    name   = "name"
    values = ["amzn2-ami-hvm-*-x86_64-gp2"]
  }
}