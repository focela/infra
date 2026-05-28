# vm-ops: applications and operational services.
# Security group and compute are co-located. Mirror this layout for new VMs.

resource "aws_security_group" "vm_ops" {
  name        = "${var.prefix}-sg-vm-ops"
  description = "HTTP/HTTPS inbound; no SSH; SSM access via IAM."
  vpc_id      = aws_vpc.this.id

  ingress {
    description = "HTTP"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "HTTPS"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "WireGuard VPN"
    from_port   = 51820
    to_port     = 51820
    protocol    = "udp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    description = "Allow all outbound"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "${var.prefix}-sg-vm-ops" }
}

module "vm_ops" {
  source = "../../modules/compute"

  prefix                = var.prefix
  name                  = "vm-ops"
  instance_type         = var.vm_ops_instance_type
  subnet_id             = aws_subnet.public[0].id
  security_group_ids    = [aws_security_group.vm_ops.id]
  instance_profile_name = aws_iam_instance_profile.ec2_ssm.name
  root_volume_size      = 20
  user_data = templatefile("${path.module}/files/cloud-init-ops.sh", {
    ssh_public_key = var.ssh_public_key
  })
}
