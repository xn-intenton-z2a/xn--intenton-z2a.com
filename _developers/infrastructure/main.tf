module "iam" {
  source = "./iam"
  service_name = var.service_name
  aws_account_id = var.aws_account_id
  aws_default_region = var.aws_default_region
  deployment_role_name = var.deployment_role_name
}
module "s3" {
  source = "./s3"
  service_name = var.service_name
  aws_account_id = var.aws_account_id
  aws_default_region = var.aws_default_region
  logs_bucket_name = var.logs_bucket_name
}
