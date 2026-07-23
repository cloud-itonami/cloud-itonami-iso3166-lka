(ns marketentry.registry
  "Pure-function market-entry filing-draft + filing-submit record
  construction -- an append-only market-entry book-of-record draft.

  Like every sibling actor's registry, there is no single international
  reference-number standard for a public-procurement market-entry
  filing -- every jurisdiction assigns its own format. This namespace
  does NOT invent one; it builds a jurisdiction-scoped sequence number
  and validates the record's required fields, the same honest,
  non-fabricating discipline `marketentry.facts` uses.

  `engagement-fee-matches-claim?` is an HONEST reapplication of the
  SAME ground-truth-recompute DISCIPLINE sibling actors use (verify a
  claimed monetary total against the entity's own recorded quantity x
  unit fields), reapplied to a market-entry engagement fee line.

  `slicing-classification-evaded?` is THIS vertical's own new
  ground-truth check, grounding LKA's flagship governor check
  (`marketentry.governor/slicing-violations`): Sri Lanka's National
  Procurement Commission Procurement Guidelines - 2024 (Gazette
  Extraordinary No. 2412/01, 25 November 2024, effective 1 January 2025,
  own primary text, see `marketentry.facts`) s.4.3 'Slicing and
  Packaging of Contracts' requires slicing/packaging to be justified by
  nature/size/complexity (4.3(a)) and bars using it 'with the intention
  to fit the value of such procurement to the procurement authority
  levels of any PC or to any specific procurement method' (4.3(c)).
  This is a DIFFERENT check SHAPE from every prior sibling: not a
  turnover-scaled formula, not a flat statutory threshold, not a boolean
  registry-membership read of the supplier, not a 3-tier contract-value
  classification, not a bid-evaluation price-adjustment recompute, not a
  struck-off company-registry legal-capacity boolean, not a
  validity-window expiry-date recompute, not a pure date-precedence
  check, and not a FDI-sector set-membership check (Bhutan) -- it is a
  CROSS-RECORD AGGREGATION-RECONCILIATION check: the thing being
  independently recomputed is not any ONE record's own fields checked
  against each other, but whether MULTIPLE engagement records sharing
  the same declared `:parent-procurement-id`, summed, actually reconcile
  against a group-level declared `:aggregate-tce` (Total Cost Estimate).
  It is also entity-scope-gated, like every prior sibling's own
  scope-gate: a no-op (false) unless the engagement itself declares
  `:sliced? true` -- an engagement the operator never declared to be a
  slice/package of a larger procurement is never subject to this check
  at all, mirroring the same 'missing entity scope means the check never
  fires' discipline Bhutan's `fdi-sector-restricted?` uses for
  `:foreign-company?`. The declared TCE values and slicing-justification
  text are NEVER fabricated here -- they are always read from the
  engagements themselves, the same discipline `required-evidence-
  satisfied?` uses for evidence checklists.

  This namespace is pure data + pure functions -- no I/O, no network call
  to any real NPC e-Procurement, ROC, IRD or BOI system. It builds the
  RECORD an operator would keep, not the act of submitting a real bid or
  a real BOI investment-registration application itself (that is
  `marketentry.operation`'s `:filing/submit`, always human-gated -- see
  README Actuation)."
  (:require [clojure.string :as str]))

(defn- unsigned-certificate
  "Every certificate this actor produces is UNSIGNED -- signature is
  the market-entry operator's act, not this actor's."
  [kind subject record-id]
  {"@context" ["https://www.w3.org/ns/credentials/v2"]
   "type" ["VerifiableCredential" kind]
   "credentialSubject" {"id" subject "record" record-id}
   "proof" nil
   "issued_by_registry" false
   "status" "draft-unsigned"})

(defn- zero-pad [n w]
  (let [s (str n)]
    (str (apply str (repeat (max 0 (- w (count s))) "0")) s)))

(defn compute-engagement-fee
  "The ground-truth engagement fee for `engagement`'s own `:base-fee`
  and `:monitoring-months` x `:monthly-rate` -- a single flat
  base + months x rate calculation, not a full pricing engine."
  [{:keys [base-fee monthly-rate monitoring-months]}]
  (+ (double base-fee)
     (* (double monthly-rate) (double monitoring-months))))

(defn engagement-fee-matches-claim?
  "Does `engagement`'s own `:claimed-fee` equal the independently
  recomputed `compute-engagement-fee`?"
  [{:keys [claimed-fee] :as engagement}]
  (== (double claimed-fee) (compute-engagement-fee engagement)))

(defn slicing-classification-evaded?
  "Is `engagement` (one row from `all-engagements`, the full engagement
  directory) an unjustified or unreconciled SLICE of a larger
  procurement, per the Procurement Guidelines - 2024 s.4.3 (sourced from
  `marketentry.facts` -- the legal text is NEVER hardcoded here)?

  A no-op (false) unless `:sliced? true` -- the Guidelines' own s.4.3
  only speaks to a procurement the Procuring Entity itself has chosen to
  slice/package; an engagement never declared as a slice has no
  obligation under this section at all, the same 'missing entity scope
  means the check never fires' discipline `fdi-sector-restricted?` uses
  in the Bhutan sibling for `:foreign-company?`.

  When `:sliced? true`, violates s.4.3 (returns true) when EITHER:
    (a) no `:slicing-justification` is on file (blank or nil) -- s.4.3
        (a)'s own text: slicing/packaging 'shall be applicable... that
        are justifiable based on the nature, size and complexity'; or
    (b) the SUM of `:declared-tce` across every engagement in
        `all-engagements` that shares THIS engagement's own declared
        `:parent-procurement-id` (this engagement included) is LESS than
        this engagement's own declared `:aggregate-tce` -- i.e. the
        slices actually on file do not add up to the group's own
        declared true size, exactly the s.4.3(c) evasion pattern ('...
        with the intention to fit the value of such procurement to the
        procurement authority levels of any PC or to any specific
        procurement method')."
  [engagement all-engagements]
  (boolean
   (when (true? (:sliced? engagement))
     (let [{:keys [parent-procurement-id aggregate-tce slicing-justification]} engagement
           siblings (filter #(= parent-procurement-id (:parent-procurement-id %)) all-engagements)
           declared-sum (reduce + 0.0 (map #(double (or (:declared-tce %) 0)) siblings))]
       (or (str/blank? slicing-justification)
           (< declared-sum (double (or aggregate-tce 0))))))))

(defn register-draft
  "Validate + construct the FILING-DRAFT registration DRAFT -- the
  market-entry operator's own act of preparing a real NPC-governed bid/
  tender-response package (or a BOI investment-registration application
  package). Pure function -- does not touch any real NPC e-Procurement,
  ROC, IRD or BOI system."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "draft: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "draft: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "draft: sequence must be >= 0" {})))
  (let [draft-number (str (str/upper-case jurisdiction) "-DFT-" (zero-pad sequence 6))
        record {"record_id" draft-number
                "kind" "filing-draft"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "draft_number" draft-number
     "certificate" (unsigned-certificate "FilingDraft" draft-number draft-number)}))

(defn register-submit
  "Validate + construct the FILING-SUBMIT registration DRAFT -- the
  market-entry operator's own act of actually submitting a real bid/
  tender response (or BOI investment-registration application), always
  human-gated upstream."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "submit: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "submit: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "submit: sequence must be >= 0" {})))
  (let [submit-number (str (str/upper-case jurisdiction) "-SUB-" (zero-pad sequence 6))
        record {"record_id" submit-number
                "kind" "filing-submit"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "submit_number" submit-number
     "certificate" (unsigned-certificate "FilingSubmit" submit-number submit-number)}))

(defn append [history result]
  (conj (vec history) (get result "record")))
