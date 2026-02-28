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

**PLAN file pattern:**
- Active plans live at project root: `PLAN_<DESCRIPTION>.md`
- Each plan has user assertions verbatim at the top (non-negotiable requirements)
- If no plan file exists for the current work, create one before starting
- Never nest plans in subdirectories — always project root

**Anti-patterns to avoid:**
- Do NOT drift to side issues when a plan file defines the priority
- Do NOT silently fail and move on — throw, don't skip
- Do NOT ask obvious questions — read the plan file

## What This Repository Is

The **home page infrastructure** for intentïon at [xn--intenton-z2a.com](https://xn--intenton-z2a.com/). AWS CDK project deploying the intentïon brand website.

- **Package**: `xn--intenton-z2a.com`
- **Organisation**: `xn-intenton-z2a`
- **Entry point**: `index.js`
- **Infrastructure**: AWS CDK (CloudFront, S3, Route53, CloudTrail)
- **Sensitive file**: `intentïon brand accounts.kdbx` — KeePass database (never commit plaintext secrets)

## What This Repository Is NOT

- Not the agentic-lib SDK — this is website infrastructure only
- Not the template — that's `repository0`

## Key Architecture

- AWS CDK application stack
- Functions library
- Application constructs library
- GitHub Actions CI/CD

## Related Repositories

| Repository | Relationship |
|------------|-------------|
| `agentic-lib` | Core SDK — provides the autonomous evolution engine |
| `repository0` | Template — the experiment this website showcases |

## Test Commands

```bash
npm test              # Unit tests
./mvnw clean verify   # CDK build and validation
```

## Infrastructure

**AWS CDK** — Java-based CDK stacks for deployment.

**Always ask before writing to AWS.** Read-only AWS operations are always permitted.

Preferred path for infrastructure: CDK code → git push → GitHub Actions deploy.

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
