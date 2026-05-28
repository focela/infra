terraform {
  required_version = ">= 1.6"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }

  # Partial backend. Runtime values come from backend.hcl (gitignored).
  # Copy terraform/envs/prod/backend.hcl.example to backend.hcl, then
  # run: make tf-init e=prod
  backend "s3" {}
}

provider "aws" {
  region = var.region
}
