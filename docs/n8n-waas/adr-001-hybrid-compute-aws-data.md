# ADR-001: Hybrid Architecture — Hostinger Compute + AWS Managed Data Layer

**Status:** Accepted
**Date:** 2026-05-26

---

## Context

Building an n8n workflow automation platform as a service (WaaS) for multiple clients. Need to balance:

- Operational cost (low revenue early on)
- Reliability (clients depend on workflows running)
- Future scalability (expect client growth)
- Operational effort (small team, minimal DevOps overhead desired)

The primary constraint is **cost efficiency at launch** while preserving a **clear migration path** to fully managed infrastructure without data migration.

---

## Decision

Deploy n8n on a **Hostinger VPS** (compute) connected to **AWS managed services** (data layer) from day one.

**Compute (Hostinger):**
- Docker Compose running n8n-main, n8n-worker, Redis, Nginx
- Redis remains local until compute migrates to AWS

**Data layer (AWS):**
- RDS Postgres — primary datastore
- S3 — n8n binary/file storage and Postgres backups
- CloudFront — CDN and DDoS protection in front of Nginx
- ACM — SSL certificate management

**Connectivity:**
- RDS publicly accessible, restricted to Hostinger static IP via security group
- SSL enforced on all RDS connections
- CloudFront hides Hostinger IP from public internet

---

## Consequences

**Positive:**
- Launch cost ~$30/mo vs ~$100/mo for fully managed AWS
- Data layer never needs to migrate — RDS and S3 endpoints stay identical when compute moves to ECS
- CloudFront provides edge protection and SSL from day one
- Docker Compose is portable — same config runs on Hostinger, EC2, or any Linux VM
- IAM + security groups enforce least-privilege access from the start

**Negative:**
- RDS publicly accessible is a weaker security posture than VPC-private
- Hostinger is a single point of failure for compute (no auto-recovery)
- Redis data is ephemeral if VM crashes (job queue only, not persistent data)
- Manual OS patching and Docker updates required on Hostinger
- No auto-scaling of workers — manual `docker compose up --scale` to add capacity

**Accepted risks:**
- RDS public access mitigated by IP whitelist + SSL requirement
- Redis queue loss on crash means in-flight executions retry or fail — acceptable at current scale
- Single VM availability acceptable while client count is low

---

## Alternatives Considered

### Option A: Fully managed AWS (ECS Fargate + RDS + ElastiCache)
- **Rejected:** ~$100/mo at launch before revenue justifies it
- All the same reliability at 3-4x the cost
- Correct choice at Phase 5 when client count or revenue grows

### Option B: Fully self-managed on Hostinger (Postgres in Docker)
- **Rejected:** Postgres in Docker requires manual backup management, no point-in-time recovery, higher data loss risk
- Saving $15/mo on RDS not worth the operational risk and recovery complexity

### Option C: AWS VPN Gateway (Site-to-Site VPN)
- **Rejected for Phase 1:** Adds ~$36/mo and significant setup complexity
- RDS stays VPC-private but Hostinger → VPC tunnel requires ongoing maintenance
- Revisit if security requirements tighten (enterprise clients, compliance)

### Option D: DigitalOcean managed Postgres
- **Considered:** Would keep everything off AWS, lower cost than RDS
- **Rejected:** Migrating off DO Postgres later adds friction; RDS is the intended final state
- Starting on RDS means zero data migration at Phase 5

---

## Migration Trigger Criteria

Move compute from Hostinger to ECS Fargate when **any** of the following are true:

| Trigger | Threshold |
|---|---|
| Monthly ARR | $5-8k/mo |
| Case studies ready | 3+ documented |
| Monthly ops burden | > 2 hrs/mo |
| First regulated client inquiry | Any law firm or healthcare lead |
| Single client SLA requirement | Any uptime guarantee |

---

## Migration Path (Phase 5) — Compute + Compliance as Single Event

Migration and compliance activation happen in the same deployment window.
Moving to AWS compute is what makes compliance controls practical to enable.
Phase 1 case studies prove delivery reliability to regulated clients.

**Compute migration:**
1. Provision ECS Fargate cluster + task definitions (same Docker image)
2. Provision ElastiCache Redis — update `QUEUE_BULL_REDIS_HOST`
3. Create ALB, wire ECS tasks as targets
4. Update CloudFront origin: Hostinger IP → ALB DNS
5. Verify traffic, cancel Hostinger VPS

**Compliance activation (same window):**
1. Enable RDS KMS encryption and CloudWatch logs
2. Enable CloudTrail (all regions, dedicated audit bucket)
3. Enable GuardDuty and AWS Config compliance rules
4. Migrate secrets from env vars to Secrets Manager
5. Enable S3 access logging
6. Sign AWS BAA (console → Artifact → Agreements)
7. Record SOC 2 observation period start date

**Market unlock (immediately after):**
- Infrastructure is now compliance-eligible
- Case studies from Phase 1 prove delivery reliability
- "SOC 2 in progress" is active and sellable to regulated clients
- Approach law firms and healthcare practices

RDS endpoint, S3 bucket, and CloudFront domain unchanged throughout.
DNS cutover downtime under 5 minutes.

---

## Infrastructure Cost at Migration

```
Pre-migration (Hostinger hybrid):    ~$47/mo at 17 clients
Post-migration (AWS managed):        ~$112/mo
Post-migration (compliance-enabled): ~$145-160/mo

Delta at $8,333/mo revenue:          ~$100/mo = 1.2% of revenue
```

---

## Related Documents

- `plan.md` — phased execution plan with unified growth timeline
- `runbook.md` — step-by-step operational setup
- `adr-002-compliance-tier-architecture.md` — compliance controls detail
- `market-strategy.md` — Phase 1 markets and expansion roadmap
