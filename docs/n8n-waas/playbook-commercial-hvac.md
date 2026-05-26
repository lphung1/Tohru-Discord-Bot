# Client Playbook — Commercial HVAC

## Client Profile

**Target:** HVAC company $5M-$15M annual revenue, commercial-primary
**Revenue mix:** 60-80% commercial (apartment complexes, car dealerships,
retail, office buildings), 20-40% residential

**Key contacts:**
- Owner / GM — decision maker, signs contract
- Office manager — day-to-day contact, workflow subject matter expert
- Dispatcher — understands job flow and scheduling pain
- Account managers — own commercial relationships

**FSM platforms at this scale:**
- ServiceTitan (most common at $5M+)
- Aspire (commercial-focused)
- Jonas Construction
- FieldEdge

Confirm which FSM they use in discovery. Integration approach varies.

---

## Discovery Call Framework

One 45-60 minute call. Goal: identify the 2-3 highest-value pain points
before proposing anything.

### Opening questions

> "Walk me through what happens from the moment a commercial client calls
> you to when the job is invoiced. Who touches it and where does it slow down?"

> "How do you currently manage renewal follow-up on your maintenance contracts?
> What does that process look like today?"

> "When a technician completes a job at an apartment complex, what does the
> property manager receive from you, and how fast do they get it?"

### What you're listening for

| What they say | What it signals |
|---|---|
| "We follow up manually when we remember" | Contract renewal automation |
| "The office manager sends emails after jobs" | Completion report automation |
| "Property managers call us to find out when the tech is coming" | Dispatch notification automation |
| "We lose track of proposals sometimes" | Proposal follow-up sequences |
| "We miss residential calls when the guys are in the field" | AI call answering (residential) |
| "Billing takes a while after jobs are done" | Invoice generation automation |

### Qualifying questions

- How many active commercial maintenance contracts do you currently hold?
- What is the average annual value of a commercial contract?
- How many jobs per day on average? Peak season?
- Do you use ServiceTitan or another FSM? How long?
- Does your office staff handle commercial communication or do account managers?

### Red flags (do not proceed)

- No FSM software (manual dispatch on paper/spreadsheet — integration not possible)
- Owner not the decision maker and no access to decision maker
- Already have an in-house ops person building automations
- Unwilling to share FSM API access

---

## Automation Priority Stack

Build in this order. Each layer delivers standalone value.

### Layer 1 — Protect Commercial Revenue (build first)

**Contract Renewal Sequences**

Trigger: Contract end date in FSM approaching
```
90 days out → Email to property manager: renewal discussion request
60 days out → Follow-up if no response: include last year's service summary
30 days out → Escalate to account manager: flag in CRM, send final notice
14 days out → Owner/GM alert if still unsigned
```

Value: One saved renewal at $25k avg > entire year of your service fee.

**Proposal Follow-Up Sequences**

Trigger: New commercial proposal sent (status in FSM or CRM)
```
Day 2  → "Just checking you received our proposal"
Day 5  → Value reinforcement: what's included, references
Day 10 → Soft urgency: "Our schedule fills quickly in [season]"
Day 21 → Final follow-up, offer a call
```

---

### Layer 2 — Commercial Operations (build second)

**Dispatch Notification to Property Manager**

Trigger: Tech dispatched in FSM
```
→ SMS + email to property manager:
  "Technician [Name] is en route to [Property].
   Estimated arrival: [Time]. Contact: [Tech phone]."

→ SMS when tech arrives on site
→ SMS when job is complete
```

Value: Eliminates "where is your tech?" calls. Makes them look enterprise.

**Job Completion Report Auto-Generation**

Trigger: Job marked complete in FSM
```
→ Pull job data from FSM API:
   - Work performed
   - Parts used
   - Tech notes
   - Photos (if FSM supports)
→ Generate PDF report
→ Email to property manager within 15 minutes of job close
→ Request digital sign-off (optional)
→ Log to client folder in S3
```

Value: Professional documentation that competitors don't provide.
Differentiator in commercial RFP responses ("we provide same-day service reports").

**Equipment Replacement Flags**

Trigger: Tech notes in FSM flag unit age or condition
```
→ Create follow-up task for account manager
→ Email to property manager:
   "During today's service visit, our technician noted [Unit X]
    is approaching end of useful life. We'd like to schedule a
    replacement consultation at your convenience."
```

Value: Turns service visits into sales opportunities systematically.

**Apartment Tenant Entry Notification**

Trigger: Job dispatched at apartment complex address
```
→ Email/SMS to property manager: tech details + arrival window
→ Property manager forwards to tenant (or system does via their PM software)
```

Note: Some property management systems (Yardi, AppFolio) have APIs.
If client uses one, integrate directly for extra value.

---

### Layer 3 — Residential Side (build third)

**AI Call Answering**

Trigger: Inbound call to main number during business hours or after hours
```
→ AI screens call:
   - Emergency or non-emergency?
   - Existing customer or new?
   - Residential or commercial?
→ Commercial calls → route to account manager voicemail + SMS alert
→ Residential emergency → escalate immediately
→ Residential non-emergency → book appointment, SMS confirmation
```

**Appointment Reminders**

Trigger: Residential job scheduled in FSM
```
24hr before → SMS + email reminder with tech name and window
2hr before  → SMS: "Your tech is on their way"
Post-job    → Review request (Google)
```

---

### Layer 4 — Reporting (build last, high retention value)

**Monthly Commercial Account Report**

Trigger: First of each month, per commercial account
```
→ Pull from FSM: all jobs in prior month for that account
→ Generate PDF:
   - Jobs completed
   - Response times
   - Parts replaced
   - Open items / follow-ups
   - Contract status and renewal date
→ Email to property manager and CC account manager
```

Value: Property managers forward this to their bosses. Your client looks
professional. Extremely high retention — no competitor does this.

---

## FSM Integration Approach

### ServiceTitan

- REST API available, requires partner access request
- Key endpoints: jobs, customers, equipment, estimates, invoices
- Webhooks available for job status changes (primary trigger mechanism)
- Setup time: 8-12hrs

### Aspire

- REST API available
- Key data: work orders, contracts, customer accounts
- Webhooks: limited — may require polling on schedule
- Setup time: 10-15hrs

### Jonas / FieldEdge

- API access varies — confirm in discovery
- May require CSV export → S3 → n8n trigger as fallback
- Setup time: 12-20hrs (less predictable)

### No FSM / Spreadsheet-based

- Do not take this client yet
- Suggest they implement ServiceTitan first
- Offer to help evaluate FSM options as a paid advisory engagement ($500-1,000)
- Return in 3-6 months once FSM is live

---

## Proposal Structure

Present after discovery call. Send within 24 hours.

### What to include

1. Summary of their specific pain points (from discovery — show you listened)
2. Automation plan by layer (use Layer 1-4 framework above)
3. Expected outcomes with numbers:
   - "Based on your 40 commercial contracts at $25k avg, Layer 1 protects $1M ARR"
   - "Completion reports delivered in 15 min vs your current [X days]"
4. Investment and timeline
5. Two options only — do not give three

### Two options

**Option A — Full Stack**
All 4 layers, full FSM integration
$6,500 setup + $2,500/mo

**Option B — Commercial Core**
Layers 1 and 2 only, FSM integration
$4,000 setup + $1,750/mo

Do not offer residential-only. This is a commercial-first engagement.

---

## Onboarding Process

### Week 1 — Access and Discovery
- [ ] FSM API credentials received
- [ ] Test FSM API connection
- [ ] Map their job statuses to trigger points (every FSM is configured differently)
- [ ] Identify commercial accounts list (for renewal sequences)
- [ ] Confirm property manager contact fields in FSM
- [ ] Identify account manager assignments per commercial client

### Week 2 — Build Layer 1
- [ ] Build contract renewal sequence
- [ ] Load commercial accounts + renewal dates
- [ ] Test with 2-3 upcoming renewals
- [ ] Client reviews and approves email copy
- [ ] Go live on renewal sequences

### Week 3 — Build Layer 2
- [ ] Build dispatch notification workflow
- [ ] Build completion report template (confirm with office manager)
- [ ] Test on 5 real jobs (review reports with client)
- [ ] Adjust report format based on feedback
- [ ] Go live on dispatch + reports

### Week 4 — Build Layer 3 (residential)
- [ ] Configure AI call answering
- [ ] Test call flows (emergency vs non-emergency, residential vs commercial routing)
- [ ] Build appointment reminder sequences
- [ ] Go live on residential automation

### Month 2 — Layer 4 + Optimization
- [ ] Build monthly report template
- [ ] Run first month report manually with client review
- [ ] Automate monthly delivery
- [ ] Review Layer 1-3 performance metrics with client
- [ ] Identify any workflow adjustments needed

---

## Success Metrics — Track and Report Monthly

| Metric | How to Measure | Target |
|---|---|---|
| Contract renewals captured on time | Renewal dates vs signed dates in FSM | >90% renewed before expiry |
| Completion reports delivery time | Job close time vs email sent time | <15 minutes |
| Proposal follow-up response rate | Replies to proposal sequences | >30% response |
| Residential calls answered | AI call log | >95% answered |
| Residential no-show rate | FSM job cancellations | <10% |
| Property manager satisfaction | Quarterly survey (1 question) | >8/10 |

Present these in the monthly report. Show trend over time.
Metrics that improve = contract renewal from the client.

---

## Expansion Opportunities (month 3+)

Once Layer 1-4 is running, upsell conversations become natural:

**Yardi / AppFolio integration**
If their apartment complex clients use property management software,
integrate directly for automated work order creation and tenant notifications.
Add-on: $2,000 setup + $300/mo per PM platform integrated.

**Equipment inventory automation**
Track parts usage from FSM, auto-generate purchase orders when stock runs low.
Add-on: $1,500 setup + $200/mo.

**Commercial sales pipeline**
n8n + CRM integration to manage new commercial prospect outreach.
Add-on: $1,500 setup + $250/mo.

**Referral from this client**
Ask at month 3 review:
> "We've built something here that most HVAC companies in your market
>  don't have. Do you know any other owners in the trades who you think
>  would benefit from this? We'd take care of them well."

Commercial HVAC owners know each other through trade associations,
buying groups (Nexstar, Service Nation), and supplier relationships.
One referral from an $8M client likely leads to another $5-10M client.

---

## Pricing Reference

| Package | Setup | Monthly | Year 1 Total |
|---|---|---|---|
| Commercial Core (Layers 1-2) | $4,000 | $1,750 | $25,000 |
| Full Stack (Layers 1-4) | $6,500 | $2,500 | $36,500 |
| Full Stack + Monthly Reports | $7,500 | $2,800 | $41,100 |

Infrastructure cost at this scale: ~$125/mo fixed + minimal variable.
Margin on Full Stack: ~$2,375/mo (~95%) after infra.
