#!/usr/bin/env bash
# Purpose: Delete the role used to create infrastructure
# Usage: ./scripts/aws-delete-infrastructure-role.sh
# Note: Requires privileges in the current environment to delete an IAM role
infrastructure_role_name='intenton-com-web-infrastructure-role'
pids=()
aws iam list-attached-role-policies \
  --role-name "${infrastructure_role_name?}" \
  --query 'AttachedPolicies[].PolicyArn' \
  --output json \
  | jq --raw-output '.[]' \
  | while read -r policy_arn ; do \
      aws iam detach-role-policy --role-name "${infrastructure_role_name?}" --policy-arn "${policy_arn?}" ; \
    done &
pids+=($!)
for pid in ${pids[*]}; do
    wait "${pid?}"
done
aws iam delete-role --role-name "${infrastructure_role_name?}"
aws iam delete-policy --policy-arn 'arn:aws:iam::541134664601:policy/infrastructure_policy'
