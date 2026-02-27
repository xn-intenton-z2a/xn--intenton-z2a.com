# intentïon xn--intenton-z2a.com - GitHub Copilot Code Review Instructions

**Last Updated:** 2026-02-27

## About This File

This file contains guidelines for **GitHub Copilot** code review agent. The repository also has guidelines for other AI coding assistants:
- `CLAUDE.md` — Guidelines for Claude Code (emphasis on autonomous task execution & implementation)

Each assistant has complementary strengths — GitHub Copilot is optimized for code review, analysis, and providing thoughtful feedback.

## What This Repository Is

The **home page infrastructure** for intentïon at [xn--intenton-z2a.com](https://xn--intenton-z2a.com/). An AWS CDK project deploying the intentïon brand website.

- **Package**: `xn--intenton-z2a.com`
- **Entry point**: `index.js`
- **Infrastructure**: AWS CDK (CloudFront, S3, Route53, CloudTrail)
- **Environments**: dev, live (separate bootstrap, build-deploy, and destroy workflows)
- **No agentic workflows** — this is a standard infrastructure project

## Code Review Philosophy

### Infrastructure-First Review

This is purely an infrastructure project. All reviews should focus on:

1. **AWS resource configuration** — correct, secure, cost-effective
2. **IAM permissions** — least privilege, properly scoped
3. **Deployment safety** — correct environment targeting, no cross-contamination
4. **CDK best practices** — proper construct usage, removal policies, tagging

### Analysis Over Execution

Prioritize static analysis:
1. **Trace CDK construct trees** — understand what resources are created
2. **Validate IAM policies** — least privilege, no wildcards
3. **Check cost implications** — new resources, scaling, logging volume
4. **Verify environment separation** — dev vs live parameters
5. **Assess deployment safety** — destroy workflows scoped correctly

## Repository Patterns and Conventions

### Infrastructure Architecture

- **CDK stacks** deploy CloudFront, S3, Route53, and CloudTrail
- **Functions library** — Lambda functions for the application
- **Application constructs** — reusable CDK construct library
- **Deployment workflows**: `dev-build-deploy.yml`, `live-build-deploy.yml`
- **Infrastructure workflows**: `infrastructure-apply.yml`, `infrastructure-destroy.yml`
- **AWS access checks**: `check-aws-access-for-*.yml`

### Key Workflows

| Workflow | Purpose | Risk Level |
|----------|---------|------------|
| `dev-bootstrap.yml` | Bootstrap dev environment | Medium |
| `dev-build-deploy.yml` | Deploy to dev | Medium |
| `dev-destroy.yml` | Tear down dev | High |
| `live-bootstrap.yml` | Bootstrap live | High |
| `live-build-deploy.yml` | Deploy to live | High |
| `infrastructure-apply.yml` | Apply infra changes | High |
| `infrastructure-destroy.yml` | Destroy infrastructure | Critical |

### Code Style

- **Java (CDK)**: Follow existing formatting conventions
- Match existing local style. Avoid unnecessary formatting changes.

## Code Review Focus Areas

### Security (CRITICAL)

- **IAM roles**: Least privilege — no `Resource: "*"` without justification
- **Public access**: S3 bucket policies, CloudFront OAI/OAC configuration
- **OIDC trust**: Deployment roles scoped to this repository
- **Secrets**: AWS Secrets Manager, never plaintext in code or variables
- **CloudTrail**: Audit logging configuration correct and complete
- **KeePass file**: `intentïon brand accounts.kdbx` must never be decrypted to plaintext in code

### Cost

- CloudFront distribution pricing tier
- S3 storage and request pricing
- CloudWatch/CloudTrail log volume
- Route53 hosted zone and query charges

### Deployment Safety

- **Environment separation**: Dev and live workflows correctly isolated
- **Destroy protection**: Destroy workflows appropriately gated
- **Bootstrap idempotency**: Bootstrap workflows safe to re-run
- **Rollback capability**: Can a failed deployment be recovered?

### Removal Policies

- Use `DESTROY` for all resources (not `RETAIN`)
- Data protection comes from backups, not CloudFormation refusal to delete
- Exception: Lambda Versions with provisioned concurrency (RETAIN for AWS bug workaround)

## Recommended Review Checklist

- [ ] **Read the PR description** — What infrastructure is changing?
- [ ] **Trace CDK constructs** — What resources are created/modified/deleted?
- [ ] **Check IAM** — Least privilege? No wildcards?
- [ ] **Validate environment targeting** — Correct dev/live separation?
- [ ] **Assess cost** — New resources priced appropriately?
- [ ] **Verify removal policies** — DESTROY (not RETAIN)?
- [ ] **Check secrets handling** — Secrets Manager, not plaintext?
- [ ] **Review workflow triggers** — Correctly gated and scoped?

## Resources

- **README**: [`./README.md`](../README.md)
- **Package Scripts**: [`./package.json`](../package.json)
- **Workflows**: [`./.github/workflows/`](./workflows/)
- **Dependabot**: [`./.github/dependabot.yml`](./dependabot.yml)

---

**Remember**: This is live infrastructure for a public website. Changes have real cost and security implications. Review IAM policies and deployment scoping with particular care.
