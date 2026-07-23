# Business Model: Independent Public-Sector Market-Entry & Procurement Compliance Service — Sri Lanka

## Classification

- Repository: `cloud-itonami-iso3166-lka`
- ISO 3166: `LKA` (Sri Lanka)
- Activity: public-procurement market-entry and ongoing regulatory-
  compliance navigation for an already-incorporated operator
- Social impact: [:sri-lankan-sme-market-access :public-spend-transparency :cross-border-friction-reduction]

## Customer

- an already-incorporated `cloud-itonami-cofog-{code}` /
  `cloud-itonami-isco-{code}` / `cloud-itonami-unspsc-{segment}` /
  `cloud-itonami-{ISIC}` operator wanting to bid on a Sri Lankan
  public contract
- a foreign SME or civic-tech vendor entering the public sector in
  Sri Lanka for the first time
- a `cloud-itonami-M6910` client that has just completed incorporation and
  now needs public-sector market access

## Offer

- registration walkthrough and bid-support checklist under the National
  Procurement Commission (NPC, `nprocom.gov.lk`)'s own **Procurement
  Guidelines - 2024 on Goods, Works, and Non-Consulting Services**
  (Gazette Extraordinary No. 2412/01, 25 November 2024, effective 1
  January 2025) -- this supersedes an earlier, less current pass at this
  document, which had cited superseded "Department of Public Finance
  directives (PFD 08/2019(ii))" framing; the NPC's own Guidelines text
  itself states it supersedes the Procurement Guidelines - 2006 and ALL
  related National Procurement Agency/General Treasury manuals,
  supplements, circulars and instructions, including that framing
- business/tax registration checklist: an entry with the Department of
  the Registrar of Companies (ROC, established 1938) via the eROC online
  incorporation system, under the Companies Act No. 07 of 2007, plus
  Inland Revenue Department (IRD) Taxpayer Identification Number (TIN)
  registration under the Inland Revenue Act No. 24 of 2017 ss.102/103
- anti-fragmentation ("slicing") reconciliation check for procurements
  the operator itself declares as one slice/package of a larger
  Master Procurement Plan item, per NPC Procurement Guidelines - 2024
  s.4.3 -- this vertical's flagship governor check (see
  `src/marketentry/registry.cljc`)
- Board of Investment of Sri Lanka (BOI) registration walkthrough for
  foreign-invested engagements, under the Board of Investment Law No. 4
  of 1978 (as renamed by Act No. 49 of 1992)
- ongoing regulatory-change monitoring subscription
- compliance-audit export package for the client's own records

## Revenue

- per-engagement market-entry fee (one-time registration + checklist
  completion)
- recurring regulatory-change monitoring subscription
- compliance-audit export package

## Trust Controls

- any actual portal registration or filing submission requires
  Market-Entry Compliance Governor clearance and always escalates to
  human sign-off (`:filing/submit` is never automated at any phase)
- a false or fabricated regulatory-requirement claim is a HARD hold that
  cannot be overridden by human approval alone — it must be corrected
  against a cited official source first
- this service does **not** provide legal or tax advice; characterization
  and filing on the client's behalf beyond checklist/draft assistance
  routes to Sri Lankan-licensed counsel or a registered agent
- every requirement cites the official portal or regulation, never
  invented

## Boundary with adjacent actors (read before forking)

- **`com-etzhayyim-ooyake`** (etzhayyim/root): read-only civic-wayfinding
  mirror of government structure, non-commercial, barred from acting as
  or for the government (G3 impersonation ban). This blueprint is
  commercial and never claims to be an official channel.
- **`matsurigoto`** (etzhayyim/root): sovereign e-government statecraft —
  literally the government, for etzhayyim's own covenant or an adopting
  nation-state. This blueprint is an independent operator the government
  contracts with or that bids into its procurement — never the
  government.
- **`com-etzhayyim-toritsugi`** (etzhayyim/root): guides a consenting
  INDIVIDUAL citizen through their OWN procedure, non-profit,
  donation-only. This blueprint's client is a business operator, not an
  individual citizen, and it is commercial.
- **`legal-entity.etzhayyim.com`**: read-only aggregated company-registry
  data, no execution. This blueprint executes (gated) registrations.
- **`cloud-itonami-M6910`**: helps a client BECOME a legal entity
  (incorporation, ISIC 6910) — a prior, different regulatory phase
  (company law). This blueprint assumes incorporation is already done and
  handles public-procurement market entry (a different regulatory domain).
- **`cloud-itonami-cofog-{code}`**: a jurisdiction-agnostic operator
  template for ONE public function. This blueprint is the orthogonal
  jurisdiction-specific axis — the two compose (fork a COFOG-function
  blueprint AND this one to operate in Sri Lanka).
