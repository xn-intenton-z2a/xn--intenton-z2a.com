#!/usr/bin/env bash
# Purpose: Export the source code to date stamped files.
# Usage: ./scripts/export-source.sh
mkdir -p './exports/'
find "." -type f -not -path '*/programs/*' -not -path '*/target/*' -not -path '*/.terragrunt-cache/*' -not -path '*/results/*' -not -path '*/actions/*' -not -path '*/exports/*' -not -path '*/coverage/*' -not -path '*/node_modules/*' -not -path '*/\.git/*' -not -path '*/\.idea/*' -print | grep -v '.DS_Store' > "./exports/$(date +%Y-%m-%d)-files-list.txt"
find "." -maxdepth 1 -type f -name '*.md' -print -exec echo "==== Content of {} ====" \; -exec cat {} \; > "./exports/$(date +%Y-%m-%d)-root-cat.txt"
find "." -maxdepth 1 -type f -name '*.txt' -print -exec echo "==== Content of {} ====" \; -exec cat {} \; >> "./exports/$(date +%Y-%m-%d)-root-cat.txt"
find "." -maxdepth 1 -type f -name '*.sh' -print -exec echo "==== Content of {} ====" \; -exec cat {} \; >> "./exports/$(date +%Y-%m-%d)-root-cat.txt"
find "./public" -type f -name '*.html' -print -exec echo "==== Content of {} ====" \; -exec cat {} \; > "./exports/$(date +%Y-%m-%d)-src-cat.txt"
find "./public" -type f -name '*.js' -print -exec echo "==== Content of {} ====" \; -exec cat {} \; >> "./exports/$(date +%Y-%m-%d)-src-cat.txt"
