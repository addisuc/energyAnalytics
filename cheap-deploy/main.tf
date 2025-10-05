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

# Key Pair
resource "aws_key_pair" "main" {
  key_name   = "energyflow-dev-key"
  public_key = file("~/.ssh/id_rsa.pub")
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
  
  # Upload application files
  provisioner "file" {
    source      = "../docker-compose.prod.yml"
    destination = "/home/ec2-user/app/docker-compose.prod.yml"
    
    connection {
      type        = "ssh"
      user        = "ec2-user"
      private_key = file("~/.ssh/id_rsa")
      host        = self.public_ip
    }
  }
  
  provisioner "file" {
    source      = "../nginx.conf"
    destination = "/home/ec2-user/app/nginx.conf"
    
    connection {
      type        = "ssh"
      user        = "ec2-user"
      private_key = file("~/.ssh/id_rsa")
      host        = self.public_ip
    }
  }
  
  provisioner "file" {
    source      = "../target/weather-service-1.0.0.jar"
    destination = "/home/ec2-user/app/weather-service-1.0.0.jar"
    
    connection {
      type        = "ssh"
      user        = "ec2-user"
      private_key = file("~/.ssh/id_rsa")
      host        = self.public_ip
    }
  }
  
  provisioner "file" {
    source      = "../frontend/dist/"
    destination = "/home/ec2-user/app/frontend/"
    
    connection {
      type        = "ssh"
      user        = "ec2-user"
      private_key = file("~/.ssh/id_rsa")
      host        = self.public_ip
    }
  }
  
  # Deploy application
  provisioner "remote-exec" {
    inline = [
      "cd /home/ec2-user/app",
      "chmod +x deploy.sh",
      "./deploy.sh"
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