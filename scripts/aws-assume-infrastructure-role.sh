#!/usr/bin/env bash
# Purpose: Assume a role for the terraform execution
# Usage: source ./scripts/aws-assume-infrastructure-role.sh
# Note: Requires privileges in the current environment to assume the role
# shellcheck disable=SC2016
unset AWS_ACCOUNT_ID
unset AWS_ACCESS_KEY_ID
unset AWS_SECRET_ACCESS_KEY
unset AWS_SESSION_TOKEN
unset AWS_DEFAULT_REGION

export AWS_ACCOUNT_ID='541134664601'
role_to_assume="arn:aws:iam::${AWS_ACCOUNT_ID?}:role/intenton-com-web-infrastructure-role"
session_name="WorkstationSession-for-${USER?}"

assume_role_output=$(aws sts assume-role \
  --role-arn "${role_to_assume?}" \
  --role-session-name "${session_name?}")

access_key=$(echo $assume_role_output | jq -r '.Credentials.AccessKeyId')
secret_access_key=$(echo $assume_role_output | jq -r '.Credentials.SecretAccessKey')
session_token=$(echo $assume_role_output | jq -r '.Credentials.SessionToken')
export AWS_ACCESS_KEY_ID=$access_key
export AWS_SECRET_ACCESS_KEY=$secret_access_key
export AWS_SESSION_TOKEN=$session_token
export AWS_DEFAULT_REGION='eu-west-2'

aws sts get-caller-identity
