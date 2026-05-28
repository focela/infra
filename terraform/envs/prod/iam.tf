# IAM role for EC2 instances. Grants AWS Systems Manager access so that
# Session Manager can be used instead of opening port 22.
data "aws_iam_policy_document" "ec2_assume" {
  statement {
    actions = ["sts:AssumeRole"]

    principals {
      type        = "Service"
      identifiers = ["ec2.amazonaws.com"]
    }
  }
}

resource "aws_iam_role" "ec2_ssm" {
  name               = "${var.prefix}-ec2-ssm-role"
  assume_role_policy = data.aws_iam_policy_document.ec2_assume.json

  tags = { Name = "${var.prefix}-ec2-ssm-role" }
}

resource "aws_iam_role_policy_attachment" "ssm_core" {
  role       = aws_iam_role.ec2_ssm.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"
}

resource "aws_iam_instance_profile" "ec2_ssm" {
  name = "${var.prefix}-ec2-ssm-profile"
  role = aws_iam_role.ec2_ssm.name
}

# Allow EC2 instances to write backups to the S3 backup bucket.
data "aws_iam_policy_document" "s3_backup" {
  statement {
    sid    = "BackupBucketAccess"
    effect = "Allow"

    actions = [
      "s3:PutObject",
      "s3:GetObject",
      "s3:DeleteObject",
      "s3:ListBucket",
    ]

    resources = [
      aws_s3_bucket.backups.arn,
      "${aws_s3_bucket.backups.arn}/*",
    ]
  }
}

resource "aws_iam_role_policy" "s3_backup" {
  name   = "${var.prefix}-s3-backup"
  role   = aws_iam_role.ec2_ssm.id
  policy = data.aws_iam_policy_document.s3_backup.json
}
