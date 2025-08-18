terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
  required_version = ">= 1.2.0"
}

# Configure the AWS Provider for an African region (af-south-1 is Cape Town)
provider "aws" {
  region = "af-south-1"
}

# Create a security group for the EC2 instance
resource "aws_security_group" "booking_platform_sg" {
  name        = "booking-platform-sg"
  description = "Security group for Booking Platform application"

  # Allow inbound traffic on port 9000 for the application
  ingress {
    from_port   = 9000
    to_port     = 9000
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "Application port"
  }

  # Allow SSH access for management
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "SSH access"
  }

  # Allow all outbound traffic
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
    description = "Allow all outbound traffic"
  }

  tags = {
    Name = "booking-platform-sg"
  }
}

# Get the latest Amazon Linux 2 AMI
data "aws_ami" "amazon_linux_2" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["amzn2-ami-hvm-*-x86_64-gp2"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }
}

# Create an EC2 instance
resource "aws_instance" "booking_platform" {
  ami                    = data.aws_ami.amazon_linux_2.id
  instance_type          = "t2.micro"
  vpc_security_group_ids = [aws_security_group.booking_platform_sg.id]
  
  # User data script to install Docker and run the application
  user_data = <<-EOF
              #!/bin/bash
              # Update system packages
              yum update -y
              
              # Install Docker
              amazon-linux-extras install docker -y
              systemctl start docker
              systemctl enable docker
              
              # Install Docker Compose
              curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
              chmod +x /usr/local/bin/docker-compose
              
              # Create app directory
              mkdir -p /app
              
              # Create docker-compose.yml file
              cat > /app/docker-compose.yml << 'COMPOSE'
              version: '3.8'
              services:
                postgres:
                  image: postgres:14-alpine
                  container_name: booking-platform-db
                  environment:
                    POSTGRES_DB: booking_platform
                    POSTGRES_USER: postgres
                    POSTGRES_PASSWORD: postgres
                  ports:
                    - "5432:5432"
                  volumes:
                    - postgres-data:/var/lib/postgresql/data
                  restart: always
                  healthcheck:
                    test: ["CMD-SHELL", "pg_isready -U postgres"]
                    interval: 10s
                    timeout: 5s
                    retries: 5
                
                app:
                  image: ${var.docker_image_name}
                  container_name: booking-platform-app
                  depends_on:
                    postgres:
                      condition: service_healthy
                  environment:
                    - SPRING_PROFILES_ACTIVE=prod
                    - DB_HOST=postgres
                    - DB_PORT=5432
                    - DB_USERNAME=postgres
                    - DB_PASSWORD=postgres
                  ports:
                    - "9000:9000"
                  restart: always
              
              volumes:
                postgres-data:
              COMPOSE
              
              # Start the application
              cd /app
              docker-compose up -d
              EOF

  tags = {
    Name = "booking-platform"
  }
}

# Define a variable for the Docker image name
variable "docker_image_name" {
  description = "The Docker image name for the booking platform application"
  type        = string
  default     = "booking-platform:latest"
}

# Output the public hostname of the EC2 instance
output "instance_hostname" {
  description = "Public hostname of the EC2 instance"
  value       = aws_instance.booking_platform.public_dns
}

# Output the public IP address of the EC2 instance
output "instance_ip" {
  description = "Public IP address of the EC2 instance"
  value       = aws_instance.booking_platform.public_ip
}