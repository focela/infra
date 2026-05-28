#!/bin/bash
# cloud-init bootstrap for vm-ops.
# Runs once on first boot. Installs Docker CE, creates the edge network,
# and installs the AWS CLI v2 (required by backup scripts).
#
# This file is a Terraform templatefile. Shell variables using curly-brace
# syntax are escaped with a double-dollar prefix so Terraform does not
# interpret them as template interpolations.
#
# shellcheck disable=SC1091  # /etc/os-release exists only on Ubuntu runtime
# shellcheck disable=SC1083,SC2288,SC2154  # Terraform templatefile directives
set -euo pipefail

# Docker CE
apt-get update -y
apt-get install -y ca-certificates curl make

install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg \
  -o /etc/apt/keyrings/docker.asc
chmod a+r /etc/apt/keyrings/docker.asc

. /etc/os-release
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] \
  https://download.docker.com/linux/ubuntu $${VERSION_CODENAME} stable" \
  | tee /etc/apt/sources.list.d/docker.list > /dev/null

apt-get update -y
apt-get install -y \
  docker-ce \
  docker-ce-cli \
  containerd.io \
  docker-buildx-plugin \
  docker-compose-plugin

systemctl enable docker
usermod -aG docker ubuntu

# Host-local Docker network used by Compose stacks.
docker network inspect edge >/dev/null 2>&1 || docker network create edge

# AWS CLI v2
apt-get install -y unzip
curl -fsSL "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" \
  -o /tmp/awscliv2.zip
unzip -q /tmp/awscliv2.zip -d /tmp/awscli-install
/tmp/awscli-install/aws/install
rm -rf /tmp/awscliv2.zip /tmp/awscli-install

# SSM agent is pre-installed on AWS Ubuntu AMIs as a snap; ensure it runs.
snap start amazon-ssm-agent || true

# WireGuard kernel requirement: allow source mark validation for routing.
# Cannot be set via Docker sysctls with network_mode: host.
echo "net.ipv4.conf.all.src_valid_mark=1" > /etc/sysctl.d/99-wireguard.conf
sysctl -p /etc/sysctl.d/99-wireguard.conf

# Optional SSH key for SSH-over-SSM clients.
# Port 22 remains closed on the security group.
%{ if ssh_public_key != "" ~}
mkdir -p /home/ubuntu/.ssh
cat >> /home/ubuntu/.ssh/authorized_keys << 'SSHKEY'
${ssh_public_key}
SSHKEY
chmod 700 /home/ubuntu/.ssh
chmod 600 /home/ubuntu/.ssh/authorized_keys
chown -R ubuntu:ubuntu /home/ubuntu/.ssh
%{ endif ~}
