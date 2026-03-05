# Claude Code Memory - intentïon xn--intenton-z2a.com

## Context Survival (CRITICAL — read this first after every compaction)

**After compaction or at session start:**
1. Read all `PLAN_*.md` files in the project root — these are the active goals
2. Run `TaskList` to see tracked tasks with status
3. Do NOT start new work without checking these first

**During work:**
- When the user gives a new requirement, add it to the relevant `PLAN_*.md` or create a new one
- Track all user goals as Tasks with status (pending → in_progress → completed)
- Update `PLAN_*.md` with progress before context gets large

**Anti-patterns to avoid:**
- Do NOT drift to side issues when a plan file defines the priority
- Do NOT silently fail and move on — throw, don't skip
- Do NOT ask obvious questions — read the plan file

## What This Repository Is

The **home page infrastructure** for intentïon at [xn--intenton-z2a.com](https://xn--intenton-z2a.com/). Single CDK stack deploying the intentïon brand website.

- **Organisation**: `xn-intenton-z2a`
- **Infrastructure**: Single AWS CDK stack (CloudFront + OAC, S3, Route53, CloudWatch Logs)
- **Sensitive file**: `intentïon brand accounts.kdbx` — KeePass database (never commit plaintext secrets)

## Key Architecture

Single flat Maven project producing one shaded JAR (`target/web.jar`):

| File | Purpose |
|------|---------|
| `infra/main/java/.../WebApp.java` | CDK entry point — reads env vars, creates stack |
| `infra/main/java/.../WebStack.java` | Single CDK stack — S3, CloudFront OAC, Route53, CloudWatch |
| `infra/test/java/.../WebStackTest.java` | CDK assertions tests |
| `public/` | Static website content (index.html, images, error pages) |
| `.github/workflows/deploy.yml` | Single deploy workflow (ci/prod auto-selection) |
| `scripts/assume-deployment-role.sh` | Local AWS role assumption |

**Environments:** `ci` (branches → `ci.web.xn--intenton-z2a.com`) and `prod` (main → `xn--intenton-z2a.com`)

**GitHub variables:** `ACTIONS_ROLE_ARN`, `DEPLOY_ROLE_ARN` (repo-level), `CERTIFICATE_ARN` (per environment)

## Test Commands

```bash
./mvnw clean verify   # Build, test, and package shaded JAR
```

## Infrastructure

**AWS CDK** — Java-based, single stack, deployed via GitHub Actions.

**Always ask before writing to AWS.** Read-only AWS operations are always permitted.

Preferred path for infrastructure: CDK code → git push → GitHub Actions deploy.

To assume the deployment role locally:
```bash
source scripts/assume-deployment-role.sh
```

## Related Repositories

| Repository | Relationship |
|------------|-------------|
| `agentic-lib` | Core SDK — provides the autonomous evolution engine |
| `repository0` | Template — the experiment this website showcases |

## Git Workflow

**You may**: create branches, commit changes, push branches, open pull requests

**You may NOT**: merge PRs, push to main, delete branches, rewrite history

**Branch naming**: `claude/<short-description>`

## Code Quality Rules

- **No unnecessary formatting** — don't reformat lines you're not changing
- **No backwards-compatible aliases** — update all callers instead
- Only run linting/formatting fixes when specifically asked

## Security Checklist

- Never commit secrets — use AWS Secrets Manager ARNs
- Never commit contents of `*.kdbx` files
- Check IAM for least privilege (avoid `Resource: "*"`)
- OIDC trust policies scoped to specific repositories
