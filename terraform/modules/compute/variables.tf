variable "prefix" {
  description = "Resource name prefix"
  type        = string
}

variable "name" {
  description = "Instance short name (e.g. vm-ops)"
  type        = string
}

variable "instance_type" {
  description = "EC2 instance type"
  type        = string
  default     = "t3.small"
}

variable "subnet_id" {
  description = "Subnet to launch the instance in"
  type        = string
}

variable "security_group_ids" {
  description = "Security group IDs to attach"
  type        = list(string)
}

variable "instance_profile_name" {
  description = "IAM instance profile name"
  type        = string
}

variable "root_volume_size" {
  description = "Root EBS volume size in GB"
  type        = number
  default     = 20
}

variable "user_data" {
  description = "Cloud-init script (plain text; provider base64-encodes it)"
  type        = string
  default     = ""
}
