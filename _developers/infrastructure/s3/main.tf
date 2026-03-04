terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 4.64.0"
    }
  }
}

provider "aws" {
  region = var.aws_default_region
}

resource "aws_s3_bucket" "logs" {
  provider = aws
  bucket = var.logs_bucket_name
  force_destroy = true
  tags = {
    service = var.service_name
  }
}
resource "aws_kms_key" "log_s3_encryption_key" {
  description             = "KMS key for S3 bucket encryption"
  deletion_window_in_days = 7
}
resource "aws_s3_bucket_server_side_encryption_configuration" "log_s3_encryption_configuration" {
  depends_on = [aws_kms_key.log_s3_encryption_key]
  bucket = var.logs_bucket_name
  rule {
    apply_server_side_encryption_by_default {
      kms_master_key_id = aws_kms_key.log_s3_encryption_key.arn
      sse_algorithm     = "aws:kms"
    }
  }
}
resource "aws_s3_bucket_policy" "logs-s3-policy" {
  depends_on = [aws_s3_bucket.logs]
  bucket = aws_s3_bucket.logs.id
  policy = jsonencode({
    Version = "2012-10-17"
    Id      = "logs-s3-policy"
    Statement = [
      {
        "Action": "s3:GetBucketAcl",
        "Effect": "Allow",
        "Resource": "arn:aws:s3:::${aws_s3_bucket.logs.bucket}",
        "Principal": { "Service": "s3.${var.aws_default_region}.amazonaws.com" }
      },
      {
        "Action": "s3:PutObject" ,
        "Effect": "Allow",
        "Resource": "arn:aws:s3:::${aws_s3_bucket.logs.bucket}/*",
        "Condition": { "StringEquals": { "s3:x-amz-acl": "bucket-owner-full-control" } },
        "Principal": { "Service": "s3.${var.aws_default_region}.amazonaws.com" }
      }
    ]
  })
}
resource "aws_s3_bucket_ownership_controls" "logs-s3-controls" {
  depends_on = [aws_s3_bucket.logs]
  bucket = aws_s3_bucket.logs.id
  rule {
    object_ownership = "BucketOwnerPreferred"
  }
}

resource "aws_s3_bucket_acl" "logs-s3-acl" {
  depends_on = [aws_s3_bucket.logs, aws_s3_bucket_ownership_controls.logs-s3-controls]
  bucket = aws_s3_bucket.logs.id
  acl    = "private"
}
