# GoHighLevel (GHL) — Platform Guide

## What It Is

GoHighLevel is a white-label agency platform. You buy it, brand it as your
own product, and give clients access to their own sub-account. Clients never
know it's GHL — they log into "YourBrand CRM."

GHL is not sold directly to small businesses. It is sold to agencies and
consultants who resell it under their own brand.

```
GoHighLevel → You (agency)
                  ↓
              Brand it "YourCompany CRM"
                  ↓
              Client sees your brand, not GHL
```

---

## What's Inside GHL

| Feature | What it does |
|---|---|
| CRM | Track leads, clients, deals, contact history |
| Email | Campaigns, sequences, follow-ups |
| SMS | Automated text messaging |
| Calendar | Booking pages, appointment reminders |
| Reviews | Request and monitor Google + Facebook reviews |
| Website chat | Chat widget for client websites |
| Funnels | Simple landing pages |
| Workflows | Basic automation (if this, do that) |
| Contracts | Send and sign agreements |
| Invoicing | Send invoices, collect payments |
| Mobile app | White-labeled app your clients use |

---

## What GHL Is Not

- Not an FSM — does not replace ServiceTitan for HVAC job management
- Not an enterprise CRM — does not replace Salesforce or HubSpot Enterprise
- Not HIPAA compliant — healthcare data cannot touch GHL
- Not SOC 2 certified — law firms will ask, GHL cannot answer
- Not a deep automation engine — that is what n8n handles

---

## GHL Pricing Tiers

| Tier | Cost/mo | What you get |
|---|---|---|
| Starter | $97 | Your agency only, 1 account |
| Unlimited | $297 | Unlimited client sub-accounts, white-label |
| SaaS Pro | $497 | Resell as your own branded SaaS product |

**SaaS Pro is the right tier** for a managed services business. Unlimited
sub-accounts and you control the branding completely.

---

## The GHL SaaS Revenue Model

At SaaS Pro you resell platform access as your own product:

```
Your cost:     $497/mo (SaaS Pro, unlimited sub-accounts)

Clients   GHL revenue/mo   Your cost   Margin/mo
───────   ──────────────   ─────────   ─────────
5         $1,250           $497        $753
10        $2,500           $497        $2,003
20        $5,000           $497        $4,503
30        $7,500           $497        $7,003
```

At 20 clients paying $250/mo for platform access, GHL alone generates
$4,500/mo margin before any management or automation fees.

This compounds as you add clients without increasing your GHL cost.

---

## How GHL Scales by Client Tier

### Small Business — Under $1M revenue
*(restaurants, small HVAC, solo operators)*

GHL replaces 5-6 tools they are probably not using well:

```
Mailchimp    → GHL email
Calendly     → GHL calendar
No CRM       → GHL CRM
No reviews   → GHL reputation management
No SMS       → GHL SMS
No chat      → GHL website chat
```

Your offering at this tier:
- GHL sub-account managed entirely by you
- Branded as your product
- Build and manage automations inside GHL
- n8n handles anything GHL cannot (complex logic, FSM integrations)

Pricing:
```
GHL access:         $200-300/mo (markup on ~$25/client cost at scale)
Management fee:     $197-297/mo
Total to client:    $397-597/mo
```

Nearly everything lives inside GHL at this tier. n8n involvement is minimal.

---

### Mid-Market — $1M-$10M revenue
*(commercial HVAC, multi-location businesses)*

At this level clients already have tools. GHL becomes a marketing and
communication layer on top of their existing FSM:

```
ServiceTitan    → stays (job management, dispatch, invoicing)
GHL             → marketing CRM, reviews, commercial follow-ups
n8n             → connects ServiceTitan events to GHL workflows
```

What GHL handles:
- Commercial contract renewal sequences
- Proposal follow-up campaigns
- Review management
- Residential lead nurturing
- Client communication history

What n8n handles:
- ServiceTitan webhook → GHL contact update
- Job completed → trigger GHL workflow
- GHL form submit → create job in ServiceTitan
- Complex multi-step logic GHL workflows cannot handle

Pricing:
```
GHL access + management:   $300-500/mo
n8n automation stack:      $1,500-2,000/mo
Setup fee:                 $6,000-8,000
Total retainer:            $1,800-2,500/mo
```

GHL is a line item inside a larger managed service at this tier.

---

### Enterprise — $10M+ revenue
*(law firms, healthcare, large commercial)*

GHL does not work here:

| Concern | Detail |
|---|---|
| Not SOC 2 certified | Law firms will ask, GHL cannot answer |
| Not HIPAA eligible | Healthcare data cannot touch GHL |
| Limited API depth | Enterprise integrations break down |
| No data residency control | GHL hosts the data |
| Enterprise CRM already in place | Cannot displace Salesforce or HubSpot |

At enterprise level: use their existing CRM + n8n for automation only.
GHL is not in the picture.

---

## GHL + n8n — How They Work Together

GHL and n8n are complementary, not competing:

```
GHL handles:                    n8n handles:
────────────────────            ──────────────────────────────
Email sequences                 ServiceTitan / Aspire API
SMS campaigns                   Complex conditional logic
Review requests                 Document generation (PDF reports)
Basic CRM workflows             Cross-platform data sync
Lead capture forms              Compliance audit logging
Calendar booking                Custom webhook processing
Reputation management           Any API GHL does not natively support
```

GHL is the client-facing platform. n8n is the back-end automation engine.

---

## Recommended Stack by Client Tier

```
Small business (< $1M):
  GHL managed + minimal n8n
  $397-597/mo all-in

Mid-market ($1M-$10M):
  GHL marketing layer + n8n FSM integration
  $1,800-2,500/mo

Enterprise ($10M+):
  Their CRM + n8n only, no GHL
  $3,500+/mo
```

---

## GHL and HVAC Specifically

HVAC marketing agencies already sell GHL to HVAC companies — it is one of
GHL's strongest verticals. A commercial HVAC client at $8M may already have
a GHL account from a previous marketing agency.

Ask in discovery:
> "Are you currently using any CRM or marketing platform —
>  GoHighLevel, HubSpot, anything like that?"

**If they already have GHL:**
- Take over management of their existing account
- Add n8n on top for ServiceTitan integration
- Lower barrier to entry — no new tool to learn
- Faster onboarding, faster time to value

**If they do not have GHL:**
- Provision them a sub-account on your white-label
- They are now on your platform, not theirs
- Stickier relationship — switching requires migrating off your branded platform

---

## Payment Processing Inside GHL

GHL has built-in payment processing (via Stripe under the hood):
- Send invoices from GHL
- Collect one-time and recurring payments
- Integrated with contracts — client signs and pays in one flow
- Replaces the PandaDoc + Stripe combo for small business clients

For mid-market and enterprise: keep PandaDoc + Stripe separate.
GHL payments are fine for clients on your GHL platform, not for
high-ticket managed service contracts.

---

## Related Documents

- `pricing.md` — tier pricing including GHL platform fees
- `market-strategy.md` — client tier breakdown
- `playbook-commercial-hvac.md` — GHL + ServiceTitan integration approach
- `payments-landing-page.md` — full payments architecture
