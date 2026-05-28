output "vpc_id" {
  description = "VPC ID"
  value       = aws_vpc.this.id
}

output "vm_ops_instance_id" {
  description = "vm-ops EC2 instance ID"
  value       = module.vm_ops.instance_id
}

output "vm_ops_public_ip" {
  description = "vm-ops Elastic IP address"
  value       = module.vm_ops.public_ip
}

output "backup_bucket" {
  description = "S3 backup bucket name"
  value       = aws_s3_bucket.backups.id
}
