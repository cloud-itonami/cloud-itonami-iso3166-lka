# cloud-itonami-iso3166-lka

Open ISO 3166 Blueprint for **LKA**: Democratic Socialist Republic of
Sri Lanka -- **`:implemented`**.

This repository designs **and implements** a forkable OSS business for
an independent public-sector market-entry consultant: an already-
incorporated operator (e.g. a `cloud-itonami-cofog-{code}`,
`cloud-itonami-isco-{code}`, `cloud-itonami-unspsc-{segment}` or
`cloud-itonami-{ISIC}` blueprint fork) gets a **MarketEntry-LLM**
advisor + independent **Market-Entry Compliance Governor** to navigate
Sri Lanka's public-procurement registration, business/tax registration
and foreign-investment rules, so the operator can win and service a
government contract (or make a foreign direct investment) without
hiring a full in-house compliance department.

Built on this workspace's
[`langgraph`](https://github.com/kotoba-lang/langgraph) StateGraph
runtime (portable `.cljc`, supervised superstep loop, interrupts,
Datomic/in-mem checkpoints) -- the same actor pattern as every prior
actor in this fleet -- here it is **MarketEntry-LLM ⊣ Market-Entry
Compliance Governor**.

> **Why an actor layer at all?** An LLM is great at drafting an
> engagement summary, normalizing intake records, and checking whether
> a claimed engagement fee actually equals base-fee + months x
> monthly-rate -- but it has no notion of which Sri Lankan procurement/
> company/tax/FDI law is official, no license to actually draft or
> submit a real bid/tender response or Board of Investment (BOI)
> registration application, and no way to independently confirm on its
> own whether a declared procurement "slice" actually reconciles against
> its own declared aggregate size under the National Procurement
> Commission's own anti-fragmentation rule, or whether Inland Revenue
> Department (IRD) Taxpayer Identification Number (TIN) registration has
> actually been verified. Letting it draft or submit directly invites
> fabricated regulatory citations, an engagement-fee mismatch being
> filed, a procurement quietly sliced below a competitive-method
> threshold, and an unverified TIN registration -- exposing the
> operator to real regulatory/legal liability. This project seals the
> MarketEntry-LLM into a single node and wraps it with an independent
> **Market-Entry Compliance Governor**, a human **approval workflow**,
> and an immutable **audit ledger**.

## Official surface

- **Public procurement**: the **National Procurement Commission (NPC)**
  of Sri Lanka (`nprocom.gov.lk`) -- a constitutional commission
  established under Article 156B(1) of Chapter XIXB of the Constitution
  of the Democratic Socialist Republic of Sri Lanka. Its own
  **Procurement Guidelines - 2024 on Goods, Works, and Non-Consulting
  Services** (Gazette Extraordinary No. 2412/01, 25 November 2024,
  published under Article 156C(1) of the Constitution) took effect
  1 January 2025, superseding the Procurement Guidelines - 2006 and all
  related National Procurement Agency/General Treasury manuals,
  supplements, circulars and instructions. **Research correction, caught
  and disclosed honestly**: the plausible-looking hostname `npc.gov.lk`
  resolves to the National **Police** Commission, an unrelated
  constitutional body -- every NPC citation in this repo uses
  `nprocom.gov.lk`, traced via `treasury.gov.lk`'s own "Procurement
  Guidelines and Manuals" page, never the identically-abbreviated but
  wrong hostname. This corrects an earlier, less current pass at this
  blueprint's `docs/business-model.md`, which had cited superseded
  "Department of Public Finance directives (PFD 08/2019(ii))" framing.
- **Business/company registration**: Department of the Registrar of
  Companies (ROC, `drc.gov.lk`), established 1938 -- online
  incorporation via the eROC system (`eroc.drc.gov.lk`). Legal basis:
  Companies Act No. 07 of 2007, Part XVI ("Registrar-General of
  Companies and Registration"), s.471(1).
- **Tax registration**: Inland Revenue Department (IRD, `ird.gov.lk`),
  Ministry of Finance -- the Taxpayer Identification Number (**TIN**),
  applied for online via `eservices.ird.gov.lk`. Legal basis: **Inland
  Revenue Act No. 24 of 2017**, Division II, s.102(1)/s.103(1).
- **Foreign Direct Investment**: Board of Investment of Sri Lanka (BOI,
  `investsrilanka.com`), the apex FDI promotion agency, established 1978
  as the Greater Colombo Economic Commission. Legal basis: **Board of
  Investment Law No. 4 of 1978** (enacted as the Greater Colombo
  Economic Commission Law, No. 4 of 1978, renamed by the Greater Colombo
  Economic Commission (Amendment) Act, No. 49 of 1992 -- both
  independently OCR'd from scanned Gazette reproductions with no native
  PDF text layer, a disclosed lower-confidence transcription; see
  `src/marketentry/facts.cljc` namespace docstring).

**Currency note**: this catalog deliberately cites the National
Procurement Commission's own 2024/2025-vintage Procurement Guidelines
(Gazette Extraordinary No. 2412/01, effective 1 January 2025) rather
than the 2006-era instrument it explicitly supersedes -- every citation
in `src/marketentry/facts.cljc` was independently fetched (`curl` +
`pdftotext -layout`, or `pdftoppm` + `tesseract` OCR where a PDF had no
text layer) and verbatim-checked against the actual government-hosted
document, not assumed from a secondary summary. See that namespace's own
docstring for the full per-citation provenance trail and the honest gaps
it explicitly declines to paper over (the BOI Law's scanned-PDF OCR
transcription; amendment Acts layered onto the 2007 Companies Act and
the 2017 Inland Revenue Act this iteration did not individually fetch;
the Sanctioned/Debarred/Blacklisted bidders list this iteration found
described in the Guidelines but did not independently fetch a live copy
of).

## Checks

Six checks, in priority order, evaluated by `marketentry.governor` on
every `MarketEntry-LLM` proposal. The first five are HARD violations
a human approver cannot override; double-actuation guards are counted
separately. The confidence/actuation gate (item 6) is SOFT -- but see
Actuation below, `:filing/draft`/`:filing/submit` never auto-commit
regardless.

| # | Check | Grounds | Source |
|---|---|---|---|
| 1 | **Spec-basis** -- a `:jurisdiction/assess`/`:filing/draft`/`:filing/submit` proposal must cite an official source, never an invented one | `marketentry.facts/spec-basis` | NPC Procurement Guidelines - 2024, ROC, IRD, BOI (see Official surface above) |
| 2 | **Evidence incomplete** -- for draft/submit, the jurisdiction's full required-evidence checklist must be on file | ROC Certificate of Incorporation, IRD TIN, Bid Security, CIDA registration, Blacklist non-membership, BOI registration | `marketentry.facts/required-evidence-satisfied?` |
| 3 | **Slicing evades classification** (flagship) -- for submit, when `:sliced? true`, independently recompute whether this slice's own declared `:parent-procurement-id` group actually reconciles under NPC Procurement Guidelines - 2024 s.4.3 (a documented slicing justification must be on file, AND the group's own declared per-slice values must sum to its own declared aggregate size); entity-scope-gated to a declared slice | `marketentry.registry/slicing-classification-evaded?` | NPC Procurement Guidelines - 2024 s.4.3 "Slicing and Packaging of Contracts" |
| 4 | **Engagement fee mismatch** -- for submit, independently recompute `claimed-fee = base-fee + monthly-rate x monitoring-months` | `marketentry.registry/engagement-fee-matches-claim?` | ground-truth recompute (fleet-standard discipline) |
| 5 | **TIN registration unverified** -- for submit, when `:requires-tin-registration? true`, independently check `:tin-registered?` | `marketentry.governor/tin-registration-unverified-violations` | Inland Revenue Act No. 24 of 2017 ss.102/103, IRD |
| 6 | **Confidence floor / actuation gate** (SOFT) -- LLM confidence below 0.6, or the op is `:filing/draft`/`:filing/submit` -> escalate to human | `marketentry.governor/check` | this vertical's own Trust Controls (`docs/business-model.md`) |

Two further double-actuation guards (`already-drafted`,
`already-submitted`) refuse to draft or submit the SAME engagement
twice, enforced off dedicated `:drafted?`/`:submitted?` booleans, never
a `:status` value.

The flagship check (3) is a genuinely different SHAPE from every prior
sibling in this fleet: a CROSS-RECORD aggregation-reconciliation test
(no single-record set-membership, no date arithmetic, no value
threshold checked against ONE record alone) -- see
`marketentry.registry`'s namespace docstring for the full shape
comparison.

## Actuation

**Drafting a real bid/tender response (or BOI investment-registration
application) and submitting a real filing are never autonomous, at any
phase, by construction.** Two independent layers enforce this:

- `marketentry.governor`'s `high-stakes` set
  (`#{:actuation/draft-filing :actuation/submit-filing}`) always
  escalates, regardless of confidence.
- `marketentry.phase`'s phase table (`phase 0` through `phase 3`)
  never puts `:filing/draft` or `:filing/submit` in any phase's
  `:auto` set -- see `marketentry.phase`'s own docstring and
  `test/marketentry/phase_test.clj`'s `filing-submit-never-auto`.

The actor may intake an engagement, assess a jurisdiction and draft a
recommendation; a human market-entry operator is always the one who
actually files a draft or a submission. `:filing/draft` and
`:filing/submit` apply SEQUENTIALLY to the SAME engagement record (draft
first, submit later) -- matching every sibling
`market-entry-compliance-governor` actor's own sequential shape.

## Core Contract

```text
engagement intake + jurisdiction facts (marketentry.facts, spec-cited)
        |
        v
   ┌────────────────────┐   proposal      ┌──────────────────────────────┐
   │ MarketEntry-LLM     │ ─────────────▶ │ Market-Entry Compliance       │
   │ (sealed)            │  + citations    │ Governor (independent system) │
   └────────────────────┘                  │ spec-basis · evidence-        │
          │                commit ◀────────┤ incomplete · slicing-         │
          │                                │ evades-classification         │
    record + ledger        escalate ◀──────┤ (FLAGSHIP) · fee-mismatch ·   │
          │            (ALWAYS for         │ tin-unverified · already-     │
          │             draft/submit)      │ drafted/submitted             │
          ▼                                └──────────────────────────────┘
      human approval
```

No automated proposal can draft or submit a filing the governor
refuses, suppress a compliance record, or claim a jurisdiction's
requirements without an official citation.

## What this is NOT

- **Not the government of Sri Lanka.** See
  [`docs/business-model.md`](docs/business-model.md) for the boundary
  with `com-etzhayyim-ooyake` (read-only civic mirror), `matsurigoto`
  (sovereign statecraft), `com-etzhayyim-toritsugi` (individual citizen
  concierge), `legal-entity.etzhayyim.com` (read-only data aggregation),
  and `cloud-itonami-M6910` (company incorporation -- a different
  regulatory phase this blueprint assumes is already complete).
- **Not legal or tax advice.** Every regulatory claim must cite the
  official source (`marketentry.facts`) and route final filings to
  Sri Lankan-licensed counsel or a registered agent where the law
  requires licensed representation.

## Run

```bash
clojure -M:dev:run     # walk a clean intake -> assess -> draft -> submit lifecycle, plus HARD-hold scenarios
clojure -M:dev:test    # governor contract · phase invariants · store parity · registry conformance · facts coverage
clojure -M:lint        # clj-kondo (errors fail; CI mirrors this)
```

## Layout

| File | Role |
|---|---|
| `src/marketentry/store.cljc` | **Store** protocol -- `MemStore` ‖ `DatomicStore` (`langchain.db` + `kotoba-lang/langchain-store`, no hand-rolled EDN-blob codec) + append-only audit ledger + draft AND submit history (dual history) |
| `src/marketentry/registry.cljc` | Filing-draft/filing-submit record construction, `engagement-fee-matches-claim?` ground-truth recompute, `slicing-classification-evaded?` flagship CROSS-RECORD aggregation-reconciliation check |
| `src/marketentry/facts.cljc` | Per-jurisdiction market-entry regulatory catalog with an official spec-basis citation per entry, honest coverage reporting |
| `src/marketentry/marketentryllm.cljc` | **MarketEntry-LLM** -- `mock-advisor`; intake/jurisdiction-assessment/draft/submit proposals |
| `src/marketentry/governor.cljc` | **Market-Entry Compliance Governor** -- 5 HARD checks + 2 double-actuation guards + 1 soft (confidence/actuation gate), see Checks above |
| `src/marketentry/phase.cljc` | **Phase 0→3** -- read-only → assisted intake → assisted assess → supervised (draft/submit always human) |
| `src/marketentry/operation.cljc` | **OperationActor** -- langgraph StateGraph |
| `src/marketentry/sim.cljc` | demo driver |
| `src/statute/facts.cljc` | general compliance-law catalog (Companies Act No. 07 of 2007, Inland Revenue Act No. 24 of 2017, Board of Investment Law No. 4 of 1978) |
| `test/marketentry/*_test.clj` | governor contract · phase invariants · store parity · registry conformance · facts coverage |
| `test/statute/*_test.clj` | statute catalog coverage/topic filters |

## No robotics premise — digital/data service exemption

Market-entry and procurement-compliance navigation is a pure
data/software service with no physical-domain work (portal registration,
document checklists, regulatory-change monitoring) -- the same
exemption class as `cloud-itonami-6310` (HR SaaS replacement) and
`cloud-itonami-gtin-*`. `blueprint.edn` sets
`:itonami.blueprint/robotics false` and `:required-technologies` lists
only real capabilities (`:identity`, `:forms`, `:dmn`, `:bpmn`,
`:audit-ledger`), no `:robotics`.

## Capability layer

Resolves via [`kotoba-lang/iso3166`](https://github.com/kotoba-lang/iso3166)
(ISO 3166 `LKA`). Required capabilities: `:identity`, `:forms`, `:dmn`,
`:bpmn`, `:audit-ledger`.

See [`docs/business-model.md`](docs/business-model.md) and
[`docs/operator-guide.md`](docs/operator-guide.md).

## Jurisdiction coverage (honest)

`marketentry.facts/coverage` reports how many requested jurisdictions
actually have an official spec-basis in `marketentry.facts/catalog`
-- currently LKA, USA and DEU are seeded. This is a starting catalog to
prove the governor contract end-to-end, not a claim of global coverage.
Adding a jurisdiction is additive: one map entry citing a real official
source -- never fabricate a jurisdiction's requirements to make coverage
look bigger.

## Maturity

`:implemented` -- see `blueprint.edn`'s
`:itonami.blueprint/implemented-slice` for the full promotion note.

## License

AGPL-3.0-or-later.

## Culture catalog

Alongside the market-entry / statute catalogs, this repo carries a
**country-level regional-culture catalog** (ADR-2607171400 addendum 2,
`cloud-itonami-municipality-culture-catalog` Wave 1, in
`com-junkawasaki/root`) — national dishes, protected products, beverages,
crafts, festivals and heritage sites for Sri Lanka:

- `src/culture/facts.cljc` — the catalog, source of truth (keyed by
  uppercase ISO3, mirroring `statute.facts`).
- `schema/culture.edn` — DataScript schema.
- `data/culture-tx.edn` — derived DataScript tx-data (regenerated from
  the catalog, never hand-edited).

City-level counterparts live in the `cloud-itonami-municipality-*` repos.
Same provenance discipline as the compliance catalogs: every entry cites a
source URL that was actually fetched and read on `:culture/retrieved-at`;
summaries state only what the cited source confirms. An item not in
`culture.facts/catalog` has no spec-basis — never fabricate one.
