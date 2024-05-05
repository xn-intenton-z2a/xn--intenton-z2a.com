remote_state {
  backend = "s3"
  config = {
    bucket = "intentiion-com-web-terraform-state"
    region = "eu-west-2"
    encrypt = true
    key = "terraform.tfstate"
  }
  generate = {
    path      = "backend.tf"
    if_exists = "overwrite_terragrunt"
  }
}
terraform {
  source = "."
}
inputs = {
  service_name = "web.xn--intenton-z2a.com"
  aws_default_region = "eu-west-2"
  aws_account_id = "541134664601"
  deployment_role_name = "xn--intenton-z2a-web-deployment-role"
  logs_bucket_name = "intentiion-com-web-logs"
}
