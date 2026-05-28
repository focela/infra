# Most recent Ubuntu 24.04 LTS AMI published by Canonical.
data "aws_ami" "ubuntu_24_04" {
  most_recent = true
  owners      = ["099720109477"] # Canonical

  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd-gp3/ubuntu-noble-24.04-amd64-server-*"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }
}

resource "aws_instance" "this" {
  ami                         = data.aws_ami.ubuntu_24_04.id
  instance_type               = var.instance_type
  subnet_id                   = var.subnet_id
  vpc_security_group_ids      = var.security_group_ids
  iam_instance_profile        = var.instance_profile_name
  associate_public_ip_address = true # used until EIP association completes
  user_data                   = var.user_data

  root_block_device {
    volume_type           = "gp3"
    volume_size           = var.root_volume_size
    encrypted             = true
    delete_on_termination = true
  }

  # IMDSv2 requires session-oriented metadata requests.
  metadata_options {
    http_tokens                 = "required"
    http_put_response_hop_limit = 1
  }

  # Cloud-init runs once on first boot; changes afterward must not
  # trigger instance replacement. AMI bumps are done deliberately.
  lifecycle {
    ignore_changes = [ami, user_data]
  }

  tags = { Name = "${var.prefix}-${var.name}" }
}

# Static public IP that persists across stop/start.
resource "aws_eip" "this" {
  domain = "vpc"
  tags   = { Name = "${var.prefix}-eip-${var.name}" }
}

resource "aws_eip_association" "this" {
  instance_id   = aws_instance.this.id
  allocation_id = aws_eip.this.id
}
