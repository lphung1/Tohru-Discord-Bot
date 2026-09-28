# MCP Server Guide — When and How to Use Them

## What MCP Is

MCP (Model Context Protocol) is how you give an AI model direct access to
tools so it can decide when and how to use them — rather than pre-defining
every step in a workflow.

```
n8n workflow:    You define every step
                 Trigger → Step 1 → Step 2 → Step 3
                 Same path every time. Deterministic.

MCP + AI agent:  AI decides which tools to use and when
                 Customer says something → AI reasons →
                 AI picks the right tool → AI acts
                 Different path every time. Adaptive.
```

---

## When You Don't Need MCP

Most standard automation doesn't need MCP:

- Email sequences → n8n + GHL
- Invoice generation → n8n + QuickBooks
- Appointment reminders → n8n schedule node
- Review requests → GHL workflow
- Dispatch notifications → n8n webhook from ServiceTitan

These are deterministic. Same input, same output, every time.

---

## When You Do Need MCP

The moment your automation needs to reason and make decisions in real time.

### AI Call Answering

Without MCP — AI can only talk:
```
Customer: "I need my AC fixed, it's making a loud noise"
AI: "I can help with that, let me transfer you" ← dead end
```

With MCP — AI can act:
```
Customer: "I need my AC fixed, it's making a loud noise"
AI uses tools:
  → look_up_customer(caller_phone_number)
  → get_service_history(customer_id)
  → check_technician_availability(zip_code, tomorrow)
  → book_appointment(customer_id, slot_id)
AI: "I've booked John for tomorrow at 2pm. Confirmation text on its way."
```

### Website Chat Agent

Without MCP — agent answers FAQs:
```
Visitor: "Do you service commercial properties in Dallas?"
Agent: "Yes we do!" ← pre-written response
```

With MCP — agent takes action:
```
Visitor: "I manage an apartment complex, can you get me a quote?"
Agent uses tools:
  → check_service_area(zip_code)
  → get_commercial_pricing(property_type, unit_count)
  → create_lead(name, email, property_details)
  → schedule_site_visit(available_slots)
Agent: "We cover your area. Based on 48 units, maintenance contracts
        start at $18,000/yr. I've booked a free site assessment Thursday
        at 10am."
```

### The Rule of Thumb

```
Scripted, predictable path:     n8n (no MCP needed)
Real-time reasoning + action:   MCP
```

---

## MCP Servers to Build (by growth stage)

### Now — Don't build any
n8n + GHL handles everything at beta and founding client stage.

### At Growth Tier — ServiceTitan MCP Server

Needed for AI call answering to actually book jobs live:

```
Tools to expose:
  search_customer(phone, name, address)
  get_service_history(customer_id)
  check_technician_availability(date, zip)
  create_job(customer_id, type, notes)
  get_open_estimates(customer_id)
  book_appointment(job_id, technician_id, slot)
```

Used by: AI call answering, website chat agent

### At Professional Tier — GHL MCP Server

Gives AI agents live CRM access:

```
Tools to expose:
  lookup_contact(phone, email)
  create_contact(name, phone, email, tags)
  add_note(contact_id, note)
  update_pipeline_stage(contact_id, stage)
```

Used by: website chat agents, lead qualification flows

### At Professional Tier — Knowledge Base MCP Server

Per-client document store the AI searches in real time:

```
Tools to expose:
  search_knowledge_base(query)
  get_pricing(service_type)
  get_service_area(zip_code)
  get_faq_answer(question)
```

Used by: website chat agents trained on each client's business

### At Enterprise — Custom per client

Salesforce, custom ERP, proprietary databases — built case by case.

---

## How It Fits the Full Stack

```
GHL          → marketing automation, CRM, client-facing platform
n8n          → deterministic workflow engine, system integrations
MCP servers  → tool access layer for AI agents
Claude/GPT   → reasoning engine that uses MCP tools

GHL + n8n handle 80% of what you sell in Phase 1.
MCP becomes relevant when AI call answering and website
chat agents need to DO things, not just say things.
```

---

## Build Order

```
Phase 1 (HVAC/restaurant beta + founding):
  No MCP — n8n + GHL only

Phase 2 (Growth tier, AI call answering live):
  ServiceTitan MCP server
  Estimated build time: 1-2 days

Phase 3 (Professional tier, website chat agents):
  GHL MCP server
  Knowledge base MCP server (per client)
  Estimated build time: 2-3 days each

Phase 4 (Enterprise):
  Custom per client
  Estimated build time: 3-5 days per integration
```

---

## MCP vs n8n HTTP Request Node

Simple rule:

| Scenario | Use |
|---|---|
| API call with known inputs/outputs | n8n HTTP Request node |
| AI needs to decide which API to call | MCP |
| Multi-step reasoning required | MCP |
| Deterministic workflow | n8n |
| Real-time conversation with tool use | MCP |

---

## Related Documents

- `plan.md` — phased execution plan
- `playbook-commercial-hvac.md` — ServiceTitan integration context
- `ghl-platform-guide.md` — GHL + n8n architecture
- `payments-landing-page.md` — billing workflow automation
