# n8n WaaS — Execution Plan (Haiku Optimized)

## Context
Hybrid architecture: Hostinger compute + AWS managed data layer.
Execute phases in order. Each phase is independently completable.

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

## Phase 5 — Migrate Compute to AWS (future)

**Trigger:** Monthly Hostinger ops burden > 2hrs OR client count > 20.

Tasks:
1. Create ECS Fargate cluster
2. Create task definitions for n8n-main and n8n-worker (same Docker image, same env vars — RDS/S3 endpoints unchanged)
3. Create ElastiCache Redis (replace local Redis)
4. Update env: `QUEUE_BULL_REDIS_HOST=<elasticache endpoint>`
5. Create ALB, target group pointing to ECS tasks
6. Update CloudFront origin: ALB DNS → replace HOSTINGER_IP
7. Verify traffic flowing through new stack
8. Cancel Hostinger VPS

**Done when:** CloudFront origin is ALB. Hostinger cancelled.

---

## Decision Checklist Before Phase 5

- [ ] Revenue covers ~$100/mo AWS managed cost
- [ ] Any single client requires SLA guarantees
- [ ] Ops time on Hostinger exceeds 2hrs/mo consistently
- [ ] Client count exceeds 20
