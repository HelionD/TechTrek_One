variable "project_name" {
  description = "Name of the project"
  type        = string
  default     = "techtrek"
}

variable "environment" {
  description = "Environment name (local, prod)"
  type        = string
  default     = "local"
}

variable "vpc_id" {
  description = "VPC ID from the VPC module"
  type        = string
}

variable "subnet_id" {
  description = "Public subnet ID to place the EC2 instance in"
  type        = string
}

variable "instance_type" {
  description = "EC2 instance type"
  type        = string
  default     = "t3.micro"
}

variable "ami_id" {
  description = "AMI ID to use for the instance (Ubuntu 22.04 us-east-1)"
  type        = string
  default     = "ami-0c7217cdde317cfec"
}

variable "key_name" {
  description = "Name of the SSH key pair to access the instance"
  type        = string
}

variable "volume_size" {
  description = "Root volume size in GB"
  type        = number
  default     = 20
}
