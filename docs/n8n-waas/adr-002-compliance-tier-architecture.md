# ADR-002: Compliance Tier Architecture for Regulated Industries

**Status:** Accepted
**Date:** 2026-05-26
**Supersedes:** Portions of ADR-001 for regulated client segments

---

## Context

ADR-001 established a hybrid Hostinger + AWS architecture optimized for cost at small scale. That architecture is not suitable for clients in regulated industries (law firms, healthcare, financial services) due to:

- RDS publicly accessible
- Compute on Hostinger (no SOC 2, no encryption guarantee, EU data residency risk)
- No audit logging
- No vendor BAA available from Hostinger

A second tier is required for regulated clients that provides full AWS isolation, audit trails, and a path to SOC 2 Type II certification.

---

## Decision

Operate two parallel deployment tiers:

**Standard Tier** (ADR-001) — SMB, non-regulated clients
**Compliance Tier** (this ADR) — law firms, healthcare, financial services

Compliance tier runs in a **dedicated AWS account** (not shared with standard tier), fully within the AWS VPC, with no public-facing compute or database endpoints.

---

## Compliance Tier Architecture

```
[Internet]
    |
[CloudFront + WAF]
    |
[ALB]  ← internal to VPC only
    |
[ECS Fargate]          [Private Subnets]
  n8n-main    ───────► RDS Postgres (Multi-AZ, encrypted)
  n8n-workers ───────► ElastiCache Redis (encrypted)
                        S3 (KMS encrypted, access logged)

[CloudTrail] ── logs ──► S3 audit bucket (separate, locked)
[AWS Config] ── compliance rules
[Secrets Manager] ── all credentials (no env var secrets)
[KMS] ── encryption keys for RDS, S3, ElastiCache
```

No compute, database, or cache endpoint is reachable from the public internet.

---

## AWS Configuration Requirements

### Compute (ECS Fargate)
```
- Launch type: FARGATE
- VPC: dedicated compliance VPC (separate from standard tier)
- Subnets: private only (no public IP assignment)
- Security group: inbound from ALB only, no direct internet
- Secrets: all via Secrets Manager (no plaintext env vars)
- Task role: least-privilege IAM (S3 bucket + RDS only)
```

### RDS Postgres
```
- publicly_accessible = false
- storage_encrypted = true (KMS customer-managed key)
- multi_az = true
- deletion_protection = true
- backup_retention_period = 35 days
- enabled_cloudwatch_logs = [postgresql, upgrade]
- ca_cert_identifier = rds-ca-2019 (enforce SSL)
- parameter: rds.force_ssl = 1
```

### S3
```
- server_side_encryption = aws:kms (customer-managed key)
- versioning = enabled
- access_logging = enabled (dedicated audit bucket)
- bucket_policy: deny all non-SSL requests
- public_access_block = all true
- object_lock = enabled (for legal hold capability)
```

### ElastiCache Redis
```
- at_rest_encryption = true
- transit_encryption = true
- auth_token = enabled
- automatic_failover = true (Multi-AZ)
- subnet: private only
```

### Audit and Monitoring
```
CloudTrail:
  - multi_region = true
  - include_global_services = true
  - log_file_validation = true
  - s3_bucket = dedicated audit bucket (separate account or locked policy)
  - retention = 7 years (legal requirement)

CloudWatch:
  - RDS slow query logs
  - ECS task logs
  - ALB access logs
  - Metric alarms: CPU, error rates, failed login attempts

AWS Config:
  - Rules: rds-storage-encrypted, s3-bucket-ssl-requests-only,
           iam-no-inline-policy, mfa-enabled-for-console-access
  - Continuous compliance monitoring

GuardDuty:
  - Enabled on compliance account
  - Findings → SNS → alert channel
```

### Secrets Management
```
All credentials in AWS Secrets Manager:
  - n8n/encryption-key
  - rds/admin-credentials
  - client/{client-id}/credentials

ECS tasks retrieve secrets at runtime via task role.
No secrets in environment variables, Docker images, or git.
```

---

## Separate AWS Account Strategy

Compliance tier runs in its own AWS account for:
- Hard blast radius isolation (standard tier breach cannot reach compliance data)
- Separate CloudTrail (audit logs cannot be tampered with from standard account)
- Easier SOC 2 scope reduction (auditors only review compliance account)
- Separate billing for cost allocation to compliance clients

AWS Organizations structure:
```
Root
├── Standard Account    ← ADR-001 architecture
└── Compliance Account  ← this ADR
```

---

## SOC 2 Type II Path

SOC 2 is required before enterprise law firms or healthcare clients will sign contracts.

| Milestone | Timeline | Cost (approx) |
|---|---|---|
| Implement controls in this ADR | Month 1-2 | Engineering time |
| Engage SOC 2 auditor | Month 2 | $15-30k total |
| Observation period begins | Month 2 | — |
| Observation period ends (6 months min) | Month 8 | — |
| SOC 2 Type II report issued | Month 9 | Included above |
| Annual renewal | Ongoing | $10-15k/yr |

Controls covered by this architecture:
- CC6.1 Logical access controls ✓
- CC6.6 Network security ✓
- CC6.7 Encryption in transit and at rest ✓
- CC7.2 Monitoring and anomaly detection ✓
- A1.2 Availability monitoring ✓

Controls requiring process/policy work (not architecture):
- CC1.x Control environment (written policies)
- CC2.x Communication and training
- CC9.x Risk management
- PI1.x Processing integrity

---

## HIPAA Compliance (Healthcare Clients)

If compliance tier handles PHI (Protected Health Information):

1. Sign AWS Business Associate Agreement (BAA) — available in AWS console, free
2. Use only HIPAA-eligible AWS services (RDS, S3, ECS, ElastiCache, CloudWatch all qualify)
3. Enable RDS encryption (required)
4. Ensure CloudWatch logs do not contain PHI in plaintext
5. Document data flows in HIPAA Risk Assessment
6. Sign BAA with any sub-processors (n8n itself is self-hosted, no vendor BAA needed)

---

## Consequences

**Positive:**
- Defensible to enterprise law firm and healthcare procurement teams
- AWS handles physical security, certifications (ISO 27001, SOC 2, HIPAA, FedRAMP)
- No public endpoints — attack surface minimal
- Audit trail meets 7-year legal retention requirement
- Multi-AZ RDS provides 99.95% uptime SLA
- Clear compliance scope for SOC 2 auditor

**Negative:**
- ~$200-300/mo per compliance tenant (vs $30/mo standard)
- Requires separate AWS account management
- SOC 2 process takes 9+ months and $15-30k
- Secrets Manager adds latency at task startup (minor)
- More Terraform to maintain (separate account, separate state)

---

## Cost Estimate — Compliance Tier (per tenant)

| Component | Service | Cost/mo |
|---|---|---|
| Compute | ECS Fargate (main + 2 workers) | ~$50-80 |
| Database | RDS Postgres Multi-AZ (db.t3.small) | ~$60 |
| Cache | ElastiCache Redis (cache.t3.micro) | ~$25 |
| Load balancer | ALB | ~$20 |
| CDN + WAF | CloudFront + WAF | ~$15-30 |
| Storage | S3 + KMS | ~$5 |
| Audit | CloudTrail + Config + GuardDuty | ~$20 |
| Secrets | Secrets Manager | ~$5 |
| **Total** | | **~$200-245/mo** |

Minimum client price to cover compliance tier: **$500+/mo** (covers infrastructure + ops margin).

---

## Migration from Standard to Compliance Tier

For clients who outgrow standard tier or require compliance:

1. Provision new compliance tier stack (separate account)
2. Export n8n workflows via API (`GET /workflows`)
3. Import into compliance instance
4. Migrate credentials (re-enter — do not copy from standard)
5. Update client DNS/webhook URLs
6. Verify all workflows execute correctly
7. Decommission standard tier tenant

Data in RDS does not migrate automatically — execution history stays in standard tier. Fresh start in compliance tier is cleaner from audit perspective.

---

## Related Documents

- `adr-001-hybrid-compute-aws-data.md` — Standard tier architecture
- `plan.md` — Phased execution plan
- `runbook.md` — Standard tier operational runbook
