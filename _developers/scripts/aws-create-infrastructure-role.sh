#!/usr/bin/env bash
# Purpose: Create a user for the terraform execution
# Usage: ./scripts/aws-create-infrastructure.sh
# Note: Requires privileges in the current environment to create an IAM role (and anything else in ./infrastructure)
# shellcheck disable=SC2016
infrastructure_role_name='intention-com-web-infrastructure-role'
aws iam create-role \
  --role-name "${infrastructure_role_name?}" \
  --assume-role-policy-document file://infrastructure/infrastructure-role-trust-policy.json \
  --query 'Role.Arn'

custom_policy_arn=$(aws iam create-policy \
  --policy-name infrastructure_policy \
  --policy-document file://infrastructure/infrastructure-role-policy.json \
  --query 'Policy.Arn' \
  --output text)

policy_arns=(
  'arn:aws:iam::aws:policy/IAMFullAccess'
  'arn:aws:iam::aws:policy/AmazonS3FullAccess'
  'arn:aws:iam::aws:policy/AWSKeyManagementServicePowerUser'
  "${custom_policy_arn?}"
)

pids=()
for policy_arn in "${policy_arns[@]}"; do
  aws iam attach-role-policy \
    --role-name "${infrastructure_role_name?}" \
    --policy-arn "${policy_arn?}" &
  pids+=($!)
done
for pid in ${pids[*]}; do
    wait "${pid?}"
done
