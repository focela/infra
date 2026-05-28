variable "region" {
  description = "AWS region"
  type        = string
  default     = "ap-southeast-1"
}

variable "prefix" {
  description = "Prefix applied to every resource name"
  type        = string
  default     = "infra"
}

variable "vpc_cidr" {
  description = "VPC CIDR block"
  type        = string
  default     = "10.0.0.0/16"
}

variable "azs" {
  description = "Availability zones (must match subnet list lengths)"
  type        = list(string)
  default     = ["ap-southeast-1a", "ap-southeast-1b"]
}

variable "public_subnets" {
  description = "Public subnet CIDRs, one per AZ"
  type        = list(string)
  default     = ["10.0.1.0/24", "10.0.2.0/24"]
}

variable "private_subnets" {
  description = "Private subnet CIDRs, one per AZ"
  type        = list(string)
  default     = ["10.0.11.0/24", "10.0.12.0/24"]
}

variable "vm_ops_instance_type" {
  description = "EC2 instance type for vm-ops"
  type        = string
  default     = "t3.small"
}

variable "ssh_public_key" {
  description = "SSH public key for ubuntu authorized_keys. Empty disables SSH key setup."
  type        = string
  default     = ""
}
