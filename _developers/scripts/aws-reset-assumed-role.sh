#!/usr/bin/env bash
# SPDX-License-Identifier: AGPL-3.0-only
# Copyright (C) 2025-2026 Polycode Limited
# Purpose: Reset an assumed role back to the platform shell default
# Usage: source ./scripts/aws-reset-assumed-role.sh
unset AWS_ACCOUNT_ID
unset AWS_ACCESS_KEY_ID
unset AWS_SECRET_ACCESS_KEY
unset AWS_SESSION_TOKEN
unset AWS_DEFAULT_REGION

aws sts get-caller-identity
