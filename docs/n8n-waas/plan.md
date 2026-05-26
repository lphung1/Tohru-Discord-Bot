# n8n WaaS — Execution Plan (Haiku Optimized)

## Context
Hybrid architecture: Hostinger compute + AWS managed data layer.
Execute phases in order. Each phase is independently completable.

Phase 5 (migration) and compliance activation are a single event —
not two separate steps. When compute moves to AWS, compliance controls
go live at the same time. This unlocks regulated market clients
(law firms, healthcare) using case studies built in Phases 1-4.

---

## Unified Growth Timeline

```
Month 1-2    Beta program (5 clients, $50)
             Validate workflows and delivery process

Month 2-6    Founding clients (HVAC + restaurants)
             Build case studies, prove service reliability

Month 6-9    Hit $5-8k/mo ARR
             Migration trigger reached

Month 8-10   Phase 5: Migrate to AWS + enable compliance simultaneously
             SOC 2 observation period starts
             AWS BAA signed (HIPAA-eligible)

Month 10-14  Approach law firms and healthcare
             Pitch: proven service + case studies + SOC 2 in progress

Month 16-18  SOC 2 Type II report issued
             Close compliance tier clients at $2,500-3,500/mo

Month 18+    $100k ARR target reachable
             Regulated clients anchor recurring revenue
```

---

## Variables (set before executing any phase)

```
HOSTINGER_IP=<static ip from hostinger panel>
HOSTINGER_USER=root
DOMAIN=<yourdomain.com>
AWS_REGION=us-east-1
RDS_INSTANCE_ID=n8n-prod-db
RDS_DB_NAME=n8n
RDS_USER=n8nadmin
RDS_PASSWORD=<generate: openssl rand -base64 32>
S3_BUCKET=n8n-waas-storage
S3_BACKUP_BUCKET=n8n-waas-backups
N8N_ENCRYPTION_KEY=<generate: openssl rand -base64 32>
AWS_ACCESS_KEY_ID=<from IAM>
AWS_SECRET_ACCESS_KEY=<from IAM>
CLOUDFRONT_ORIGIN=<hostinger ip or domain>
```

---

## Phase 1 — AWS Data Layer

**Goal:** RDS, S3, IAM ready before compute is touched.

Tasks:
1. Create RDS Postgres (db.t3.micro, Postgres 15, publicly accessible, SSL required)
2. Create security group `n8n-rds-sg`: inbound 5432 from HOSTINGER_IP only
3. Attach security group to RDS instance
4. Create S3 bucket: `S3_BUCKET` (block public access, versioning off)
5. Create S3 bucket: `S3_BACKUP_BUCKET` (block public access, lifecycle: delete after 30 days)
6. Create IAM user `n8n-app`: attach inline policy (S3 read/write on both buckets only)
7. Generate IAM access key — save as AWS_ACCESS_KEY_ID / AWS_SECRET_ACCESS_KEY
8. Verify RDS reachable: `psql -h RDS_ENDPOINT -U RDS_USER -d RDS_DB_NAME`

**Done when:** psql connects from local machine.

---

## Phase 2 — Hostinger Compute

**Goal:** Docker stack running, connected to AWS data layer.

Tasks:
1. Provision Hostinger VPS (Ubuntu 22.04, 4GB RAM minimum)
2. Enable static IP in Hostinger panel
3. Run runbook: `runbook.md` (full step-by-step)

**Done when:** `curl https://DOMAIN/healthz` returns 200.

---

## Phase 3 — CloudFront + DNS

**Goal:** CloudFront in front of Nginx, SSL via ACM.

Tasks:
1. Request ACM certificate for DOMAIN (DNS validation)
2. Create CloudFront distribution:
   - Origin: HOSTINGER_IP, port 443, HTTPS only
   - Viewer: HTTPS redirect
   - Cache policy: CachingDisabled
   - Alternate domain: DOMAIN
   - SSL: ACM cert from step 1
3. Point DOMAIN DNS → CloudFront distribution domain
4. Update n8n env: `WEBHOOK_URL=https://DOMAIN`
5. Restart n8n-main container: `docker compose restart n8n-main`

**Done when:** `curl https://DOMAIN` returns n8n UI through CloudFront.

---

## Phase 4 — First Client Onboarding

**Goal:** One client workspace live.

Tasks:
1. Log into n8n UI at https://DOMAIN
2. Create owner account
3. Create Project for client (Settings → Projects → New)
4. Send client login credentials
5. Verify client can create and execute a test workflow

**Done when:** Client executes one workflow successfully.

---

## Phase 5 — Migrate Compute + Activate Compliance (single event)

**Trigger:** Monthly revenue $5-8k ARR OR ops burden > 2hrs/mo OR first
regulated client inquiry received — whichever comes first.

**This phase does two things simultaneously:**
- Moves compute from Hostinger to ECS Fargate (reliability)
- Enables compliance controls (regulated market access)

### Part A — Compute Migration
Tasks:
1. Create ECS Fargate cluster
2. Create task definitions for n8n-main and n8n-worker (same Docker image, same env vars — RDS/S3 endpoints unchanged)
3. Create ElastiCache Redis (replace local Redis)
4. Update env: `QUEUE_BULL_REDIS_HOST=<elasticache endpoint>`
5. Create ALB, target group pointing to ECS tasks
6. Update CloudFront origin: ALB DNS → replace HOSTINGER_IP
7. Verify traffic flowing through new stack
8. Cancel Hostinger VPS

### Part B — Compliance Activation (same deployment window)
Tasks:
1. Enable RDS storage encryption (KMS) — if not already enabled from Phase 1
2. Enable RDS CloudWatch logs: postgresql, upgrade
3. Enable CloudTrail (all regions, log to dedicated S3 audit bucket)
4. Enable GuardDuty on AWS account
5. Enable AWS Config with rules: rds-storage-encrypted, s3-bucket-ssl-requests-only, mfa-enabled-for-console-access
6. Move all secrets from env vars to AWS Secrets Manager
7. Enable S3 access logging on both buckets (log to audit bucket)
8. Sign AWS BAA (Business Associate Agreement) — AWS console → Artifact → Agreements
9. Document SOC 2 observation period start date

### Part C — Market Unlock (immediately after Part B)
Tasks:
1. Update case study template to include infrastructure compliance notes
2. Add "SOC 2 in progress" to service description
3. Begin outreach to law firms and healthcare practices
4. Use HVAC/restaurant case studies as proof of delivery reliability

**Done when:** CloudFront origin is ALB, CloudTrail active, AWS BAA signed,
SOC 2 observation date recorded.

---

## Decision Checklist Before Phase 5

- [ ] Monthly revenue at $5-8k ARR
- [ ] 3+ case studies documented (HVAC or restaurant)
- [ ] Ops time on Hostinger exceeds 2hrs/mo consistently
- [ ] OR first regulated client inquiry received
- [ ] RDS encryption enabled (should be from Phase 1)
- [ ] MFA enabled on AWS root and IAM admin accounts
