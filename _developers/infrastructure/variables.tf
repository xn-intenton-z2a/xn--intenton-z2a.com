variable "service_name" {
  type = string
  default = "web.xn--intenton-z2a.com"
  description = "Read from environment: TF_VAR_service_name"
}
variable "aws_default_region" {
  type = string
  default = "local"
  description = "Read from environment: TF_VAR_aws_default_region"
}
variable "aws_account_id" {
  type = string
  default = "local"
  description = "Read from environment: TF_VAR_aws_account_id"
}
variable "deployment_role_name" {
  type = string
  default = "local"
  description = "Read from environment: TF_VAR_deployment_role_name"
}
variable "logs_bucket_name" {
  type = string
  default = "local"
  description = "Read from environment: TF_VAR_logs_bucket_name"
}
