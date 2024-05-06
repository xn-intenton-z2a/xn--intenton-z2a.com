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

data "aws_iam_policy_document" "trust_policy_document" {
  statement {
    effect = "Allow"
    principals {
      type = "AWS"
      identifiers = [
        "arn:aws:iam::541134664601:user/polycode-default-account"
      ]
    }
    actions = [
      "sts:AssumeRole"
    ]
  }
  statement {
    effect = "Allow"
    principals {
      type = "Federated"
      identifiers = [
        "arn:aws:iam::${var.aws_account_id}:oidc-provider/token.actions.githubusercontent.com"
      ]
    }
    actions = [
      "sts:AssumeRoleWithWebIdentity"
    ]
    condition {
      test = "StringEquals"
      variable = "token.actions.githubusercontent.com:aud"
      values = [
        "sts.amazonaws.com"
      ]
    }
    condition {
      test = "StringLike"
      variable = "token.actions.githubusercontent.com:sub"
      values = [
        "repo:Polycode-Limited/xn--intenton-z2a.com:*"
      ]
    }
  }
}

resource "aws_iam_role" "iam_deploy_role" {
  name = var.deployment_role_name
  assume_role_policy = data.aws_iam_policy_document.trust_policy_document.json
}

resource "aws_iam_role_policy_attachment" "iam-attachment" { # 1
  depends_on = [aws_iam_role.iam_deploy_role]
  role = aws_iam_role.iam_deploy_role.name
  policy_arn = "arn:aws:iam::aws:policy/IAMFullAccess"
}
resource "aws_iam_role_policy_attachment" "route-attachment" { # 2
  depends_on = [aws_iam_role.iam_deploy_role]
  role = aws_iam_role.iam_deploy_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonRoute53FullAccess"
}
resource "aws_iam_role_policy_attachment" "cloudwatch-attachment" { # 3
  depends_on = [aws_iam_role.iam_deploy_role]
  role = aws_iam_role.iam_deploy_role.name
  policy_arn = "arn:aws:iam::aws:policy/CloudWatchFullAccessV2"
}
resource "aws_iam_role_policy_attachment" "ecr-attachment" { # 4
  depends_on = [aws_iam_role.iam_deploy_role]
  role = aws_iam_role.iam_deploy_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryFullAccess"
}
resource "aws_iam_role_policy_attachment" "ssm-attachment" { # 5
  depends_on = [aws_iam_role.iam_deploy_role]
  role = aws_iam_role.iam_deploy_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMFullAccess"
}
resource "aws_iam_role_policy_attachment" "tag-attachment" { # 6
  depends_on = [aws_iam_role.iam_deploy_role]
  role = aws_iam_role.iam_deploy_role.name
  policy_arn = "arn:aws:iam::aws:policy/ResourceGroupsandTagEditorFullAccess"
}
resource "aws_iam_role_policy_attachment" "cloudtrail-attachment" { # 7
  depends_on = [aws_iam_role.iam_deploy_role]
  role = aws_iam_role.iam_deploy_role.name
  policy_arn = "arn:aws:iam::aws:policy/AWSCloudTrail_FullAccess"
}
resource "aws_iam_role_policy_attachment" "acm-attachment" { # 8
  depends_on = [aws_iam_role.iam_deploy_role]
  role = aws_iam_role.iam_deploy_role.name
  policy_arn = "arn:aws:iam::aws:policy/AWSCertificateManagerFullAccess"
}
data "aws_iam_policy_document" "inline_policy_document" { # 9
  statement {
    effect = "Allow"
    actions = [
      "s3:*",
      "s3-object-lambda:*",
      "cloudformation:*",
      "xray:*"
    ]
    resources = [
      "*"
    ]
  }
}
resource "aws_iam_policy" "inline_policy" {
  depends_on = [aws_iam_role.iam_deploy_role]
  name = aws_iam_role.iam_deploy_role.name
  policy = data.aws_iam_policy_document.inline_policy_document.json
  tags = {
    service = var.service_name
  }
}
resource "aws_iam_role_policy_attachment" "deployment_role_attachment" {
  depends_on = [aws_iam_role.iam_deploy_role, aws_iam_policy.inline_policy]
  role = aws_iam_role.iam_deploy_role.name
  policy_arn = aws_iam_policy.inline_policy.arn
}
