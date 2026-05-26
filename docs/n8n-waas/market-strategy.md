# Market Strategy — AI Automation Consulting

## Market Expansion Roadmap

```
Phase 1 (Now):      HVAC, Restaurants
                    Low regulation, high automation ROI, fast sales cycle
                         ↓
Phase 2 (6-12mo):   Law Firms, Healthcare (dental, chiro, mental health)
                    Regulated, higher price point, longer sales cycle
                         ↓
Phase 3 (12-24mo):  Real Estate, Insurance Agencies, Education
                    Mixed regulation, large addressable market
                         ↓
Avoid:              Financial services (SEC, FINRA, SOX complexity
                    outweighs revenue opportunity at this stage)
```

---

## Phase 1 — HVAC Companies

### Why HVAC First

- High call volume with technicians constantly in the field
- A single missed call = $500-5,000 lost job
- Seasonal spikes create acute pain (summer AC emergencies, winter heating)
- Not tech-savvy — low DIY risk (they won't Zapier their way out)
- Cash-rich during peak season, willing to spend to solve problems
- Google reviews are existential — one bad review kills local SEO
- Strong word-of-mouth culture — one happy client refers others in the trades

### HVAC Pain Points → Automation Opportunities

| Pain Point | Automation Solution | Value |
|---|---|---|
| Missed calls during jobs | AI call answering — screen, book, escalate | Never lose a job to voicemail |
| Manual appointment scheduling | AI books directly into calendar | 2-3hrs/wk saved |
| No-show appointments | SMS + email reminders 24hr and 2hr before | 20-30% no-show reduction |
| Slow review collection | Automated post-service review request | 3-5x more Google reviews |
| Quote follow-up gaps | Automated follow-up sequence after estimate | 15-25% more quote conversions |
| Maintenance contract renewals | Annual reminder sequences | Recurring revenue protection |
| After-hours emergencies | AI triage — urgent vs next-day | Emergency premium captures |
| Technician dispatch notifications | Auto-notify customer when tech is 30min out | Reduces "where is he?" calls |

### HVAC Recommended Starting Package

**Lead with AI call answering** — it's the most visible, fastest ROI, easiest to sell.

Founding client pitch:
> *"How many calls do you miss while your guys are on a job? We put an AI on your phone line
> that answers, qualifies the lead, books the appointment, and texts you a summary.
> You pay nothing if it doesn't book at least 3 jobs in the first 30 days."*

Founding package for HVAC:
- AI call answering (up to 300 min/mo)
- Appointment confirmation + reminder sequences
- Post-service review request automation
- Price: $297/mo founding → $597/mo standard

**Note on call volume costs:**
Twilio + AI costs approximately $0.10-0.15/min for AI call answering.
300 min/mo = ~$30-45 in hard costs. Budget accordingly.

### HVAC Sales Approach

- Find them: Nextdoor ads, local Facebook groups, ACCA local chapter meetings, Google Maps scrape of local HVAC companies without review automation
- Decision maker: owner directly (most are owner-operated)
- Sales cycle: 1-2 weeks (fast decision, spend their own money)
- Best time to approach: just before peak season (April-May for AC, September-October for heat)
- Avoid: large franchises (corporate procurement slows everything)
- Proof point needed: 1 case study showing calls captured + jobs booked

---

## Phase 1 — Restaurants

### Why Restaurants

- High volume of repetitive communication (reservations, orders, catering)
- Staff constantly overwhelmed — automation removes load without headcount
- Catering and events is a high-margin revenue line that often has slow follow-up
- Review management is critical (Yelp and Google dominate restaurant discovery)
- Multi-location restaurants are a natural upsell path once trust is established

### Restaurant Pain Points → Automation Opportunities

| Pain Point | Automation Solution | Value |
|---|---|---|
| No-show reservations | Confirmation + reminder + deposit automation | 30-40% no-show reduction |
| Catering inquiries left unresponded | Instant AI response + qualification | Capture leads competitors miss |
| No review collection process | Post-visit SMS review request | Google/Yelp ranking improvement |
| Manual waitlist management | SMS waitlist notifications | Better guest experience |
| Staff scheduling conflicts | Shift swap notification automation | Reduce manager time |
| Social media content gaps | Auto-post specials and events | Consistent online presence |
| Email list not utilized | Monthly newsletter + promotion sequences | Direct revenue from existing customers |
| Online order errors | Order confirmation + issue escalation | Reduce bad reviews |

### Restaurant Recommended Starting Package

**Lead with catering inquiry automation + review requests** — visible ROI within days.

Founding client pitch:
> *"How many catering inquiries do you get that don't get followed up on the same day?
> We automate an instant response with your menu and pricing, qualify the lead,
> and book a call with you. Most restaurants see 2-3 extra catering bookings a month
> within the first 30 days."*

Founding package for restaurants:
- Catering inquiry auto-response and qualification
- Reservation confirmation and reminder sequences
- Post-visit review request (SMS or email)
- Price: $197/mo founding → $397/mo standard

### Restaurant Caveats

- Thin margins (3-9%) — they are price-sensitive, do not oversell
- High owner stress — keep setup and onboarding extremely simple
- Multi-location is where the money is — use single-location as a case study to pitch chains
- Avoid: franchises (corporate approval required), fine dining (low volume, high touchiness about automation feeling impersonal)
- Best targets: casual dining, fast casual, catering-focused, bars with food

### Restaurant Sales Approach

- Find them: OpenTable partner list, local restaurant association, Instagram DMs to busy local spots, walk-in introduction during off-hours (2-4pm)
- Decision maker: owner or GM
- Sales cycle: 1-3 weeks
- Proof point needed: review count increase and/or catering bookings captured

---

## Phase 2 — Law Firms

### Why Law Firms (Future)

- High billable rate means admin time is extremely costly to waste
- Client intake is manual and slow at most small firms
- Deadline management and client communication are chronic pain points
- Willing to pay premium for reliability and confidentiality
- Compliance requirement creates a moat (competitors can't easily serve them)

### Infrastructure Requirement

Compliance tier (ADR-002) required before taking law firm clients:
- AWS-only compute
- Private RDS, encrypted at rest
- CloudTrail audit logging
- SOC 2 Type II in progress or complete

**Do not onboard law firm clients on standard (Hostinger) tier.**

### Law Firm Automation Opportunities

| Automation | Value |
|---|---|
| Client intake form → CRM → conflict check notification | Hours saved per intake |
| Consultation scheduling + reminder | Reduce no-shows |
| Document request follow-up sequences | Paralegals stop chasing clients |
| Deadline reminder system (statute of limitations alerts) | Risk reduction |
| Client status update automation | Reduce "where is my case?" calls |
| Post-matter review and referral request | New client generation |
| Invoice follow-up sequences | Faster collections |

### Law Firm Pricing

Minimum: $2,500/mo + $7,500 setup (compliance tier infrastructure included)

---

## Phase 2 — Healthcare (Dental, Chiro, Mental Health)

### Why Healthcare

- Appointment no-shows cost $150-300 each (industry average 18% no-show rate)
- Patient communication is manual and staff-intensive
- Recall campaigns (hygiene reminders, follow-up appointments) are high ROI
- Mental health practices in particular are underserved by automation vendors

### Infrastructure Requirement

HIPAA compliance required:
- AWS BAA signed
- Compliance tier architecture (ADR-002)
- PHI must not be logged in plaintext in any system

### Healthcare Automation Opportunities

| Automation | Value |
|---|---|
| Appointment reminders (SMS + email) | 30-40% no-show reduction |
| Recall campaigns (6-month hygiene, annual checkup) | Direct revenue |
| New patient intake forms → EHR | Staff time saved |
| Insurance verification notification | Reduce day-of surprises |
| Post-visit satisfaction survey | Reviews + retention signal |
| Waitlist notification when cancellation occurs | Fill every slot |
| Review request (non-PHI) | Google ranking for local search |

### Healthcare Pricing

Minimum: $3,000/mo + $8,500 setup

---

## Markets to Avoid — Financial Services

Excluded from roadmap. Reasons:

| Issue | Detail |
|---|---|
| SEC/FINRA regulation | Any automated client communication may require compliance review |
| SOX requirements | Financial data handling adds audit complexity |
| Sales cycle | Months-long procurement with legal review |
| Liability exposure | Errors in financial communication = lawsuits |
| Competitive moat unclear | Large incumbents (Salesforce, Redtail) already own this space |
| Compliance cost | FINRA/SEC-aligned infrastructure far exceeds other regulated markets |

Revisit only if a specific opportunity presents itself with legal counsel involved.

---

## Market Prioritization Matrix

```
                HIGH VALUE
                    |
    Law Firms  ●    |    ● Healthcare
                    |
HARD TO ────────────┼──────────────── EASY TO
  SELL              |                   SELL
                    |
                    |    ● HVAC
               ●    |    ● Restaurants
           Real     |
           Estate   |
                    |
                LOW VALUE
```

HVAC and Restaurants: easy to sell, moderate value — volume play, build case studies
Law Firms and Healthcare: harder to sell, high value — quality play, needs compliance tier

---

## Client Acquisition by Phase

### Phase 1 (HVAC + Restaurants)

- Target: 5 founding clients total (3 HVAC, 2 restaurants)
- Channel: direct outreach, local networking, trades associations
- Proof required: working product + 1 testimonial per vertical
- Timeline: clients 1-5 in first 60 days

### Phase 2 (Law Firms + Healthcare)

- Target: 2-3 clients per vertical
- Channel: bar association events, healthcare practice management consultants, referrals from Phase 1 clients
- Proof required: SOC 2 in progress, HIPAA BAA signed, 1 law firm or healthcare case study
- Timeline: month 9-15 (after compliance infrastructure and SOC 2 observation period begins)

### Phase 3 (Real Estate + Insurance + Education)

- Expand once Phase 2 generates stable recurring revenue
- Use existing compliance tier — no new infrastructure required for real estate/insurance
- Education may require FERPA review — assess per opportunity
