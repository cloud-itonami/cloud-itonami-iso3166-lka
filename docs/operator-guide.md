# Operator Guide

## First Deployment

1. Confirm the client's incorporation/legal-entity status is complete
   (route to `cloud-itonami-M6910` or local counsel first if not).
2. Register the client's intake: business type, target public function,
   prior filing history in Sri Lanka if any.
3. Run the advisor in read-only mode against the National Procurement
   Commission (NPC, `nprocom.gov.lk`)'s own Procurement Guidelines -
   2024 (Gazette Extraordinary No. 2412/01, effective 1 January 2025).
4. Compare the checklist against the client's current documentation
   (eROC registration status; Inland Revenue Department TIN status).
5. If the engagement declares itself a slice/package of a larger
   procurement (`:sliced? true`), confirm a slicing justification is on
   file and that the group's own declared per-slice values sum to its
   own declared aggregate size (Procurement Guidelines - 2024 s.4.3) --
   the Market-Entry Compliance Governor recomputes this independently
   and HARD-holds a submit that fails either test.
6. Enable gated filing-draft assistance once the Market-Entry Compliance
   Governor contract is trusted; actual submission always requires human
   sign-off.

## Minimum Production Controls

- client-owned data store for business/tax registration documents
- clear provenance (official portal/regulation citation) for every
  requirement surfaced
- approval workflow for any portal registration or filing submission
- named referral relationship with Sri Lankan-licensed counsel or a
  registered agent for anything beyond checklist/draft assistance
- monthly audit export

## Certification

Certified operators must prove data provenance, audit traceability, that
automated actions cannot bypass the Market-Entry Compliance Governor, and
a working referral relationship with Sri Lankan-licensed counsel or a
registered agent for whatever licensed representation the law of
Sri Lanka requires for actual public-procurement filings.
