#!/usr/bin/env bash
# Purpose: Reset an assumed role back to the platform shell default
# Usage: source ./scripts/aws-reset-assumed-role.sh
unset AWS_ACCOUNT_ID
unset AWS_ACCESS_KEY_ID
unset AWS_SECRET_ACCESS_KEY
unset AWS_SESSION_TOKEN
unset AWS_DEFAULT_REGION

aws sts get-caller-identity
