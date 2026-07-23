(ns marketentry.governor
  "Market-Entry Compliance Governor -- the independent compliance layer
  that earns the MarketEntry-LLM the right to commit. The LLM has no
  notion of Sri Lankan procurement law, whether a claimed engagement fee
  actually equals base + months x rate, whether a declared procurement
  slice/package actually reconciles against its own declared aggregate
  size under the National Procurement Commission's Procurement
  Guidelines - 2024 s.4.3, whether Inland Revenue Department Taxpayer
  Identification Number (TIN) registration has been verified for a
  filing that requires it, or when a draft stops being a draft and
  becomes a real-world NPC-governed bid/tender response or BOI
  investment-registration application, so this MUST be a separate system
  able to *reject* a proposal and fall back to HOLD.

  `:itonami.blueprint/governor` is `:market-entry-compliance-governor`
  (shared family keyword on blueprints).

  This blueprint's own text (docs/business-model.md Trust Controls:
  'any actual portal registration or filing submission requires
  Market-Entry Compliance Governor clearance and always escalates to
  human sign-off'; 'a false or fabricated regulatory-requirement claim is
  a HARD hold') names exactly the checks below.

  Six checks, in priority order, ALL HARD violations: a human
  approver CANNOT override them. The confidence/actuation gate is
  SOFT: it asks a human to look (low confidence / actuation), and the
  human may approve -- but see `marketentry.phase`: for `:stake
  :actuation/draft-filing`/`:actuation/submit-filing` NO phase ever
  allows auto-commit either. Two independent layers agree that
  actuation is always a human call.

    1. Spec-basis                  -- did the jurisdiction proposal cite
                                       an OFFICIAL source
                                       (`marketentry.facts`), or invent
                                       one?
    2. Evidence incomplete         -- for `:filing/draft`/
                                       `:filing/submit`, has the
                                       jurisdiction actually been
                                       assessed with a full evidence
                                       checklist on file?
    3. Slicing evades
       classification              -- for `:filing/submit`, when the
                                       engagement declares `:sliced?
                                       true`, INDEPENDENTLY recompute
                                       whether this slice's own declared
                                       `:parent-procurement-id` group
                                       actually reconciles under the
                                       Procurement Guidelines - 2024
                                       s.4.3 (`marketentry.registry`),
                                       and HARD-hold if not. FLAGSHIP
                                       check for this jurisdiction -- a
                                       CROSS-RECORD aggregation-
                                       reconciliation check (no single-
                                       record set membership, no date
                                       arithmetic, no value threshold on
                                       ONE record alone), a check SHAPE
                                       genuinely different from every
                                       prior sibling's (turnover formula
                                       / flat threshold / boolean
                                       registry membership / 3-tier value
                                       class / bid-margin recompute /
                                       struck-off boolean / validity-
                                       window expiry recompute / date-
                                       precedence ordering / FDI-sector
                                       set-membership), and entity-SCOPE-
                                       gated (only an engagement the
                                       operator itself declared
                                       `:sliced? true` has any s.4.3
                                       obligation at all). See
                                       `marketentry.facts` /
                                       `marketentry.registry`.
    4. Engagement fee mismatch     -- for `:filing/submit`,
                                       INDEPENDENTLY recompute whether
                                       the engagement's own `:claimed-
                                       fee` equals `base-fee +
                                       monthly-rate x monitoring-
                                       months` -- honest reapplication
                                       of the ground-truth-recompute
                                       discipline sibling actors use.
    5. TIN registration
       unverified                   -- for `:filing/submit`, when the
                                       engagement declares
                                       `:requires-tin-registration?
                                       true`, INDEPENDENTLY check
                                       `:tin-registered?`. CONDITIONAL
                                       on the engagement's own ground
                                       truth. Grounded in the Inland
                                       Revenue Act No. 24 of 2017
                                       ss.102/103 Taxpayer Identification
                                       Number (TIN) registration duty
                                       owed to the Inland Revenue
                                       Department (IRD) (see
                                       `marketentry.facts`).
    6. Confidence floor / actuation
       gate                          -- LLM confidence below threshold,
                                       OR the op is `:filing/draft`/
                                       `:filing/submit` (REAL acts)
                                       -> escalate.

  Two more guards, double-draft/double-submit prevention, are enforced
  off dedicated `:drafted?`/`:submitted?` facts (never a `:status`
  value)."
  (:require [marketentry.facts :as facts]
            [marketentry.registry :as registry]
            [marketentry.store :as store]))

(def confidence-floor 0.6)

(def high-stakes
  "Stakes grave enough to always require a human, even when clean.
  Drafting a real bid/tender-response or BOI investment-registration
  package and submitting it are the two real-world actuation events
  this actor performs."
  #{:actuation/draft-filing :actuation/submit-filing})

;; ----------------------------- checks -----------------------------

(defn- spec-basis-violations
  "A `:jurisdiction/assess` (or `:filing/draft`/`:filing/submit`)
  proposal with no spec-basis citation is a HARD violation -- never
  invent a jurisdiction's market-entry requirements."
  [{:keys [op]} proposal]
  (when (contains? #{:jurisdiction/assess :filing/draft :filing/submit} op)
    (let [value (:value proposal)]
      (when (or (empty? (:cites proposal))
                (and (contains? value :spec-basis) (nil? (:spec-basis value))))
        [{:rule :no-spec-basis
          :detail "公式spec-basisの引用が無い提案は法域要件として扱えない"}]))))

(defn- evidence-incomplete-violations
  "For `:filing/draft`/`:filing/submit`, the jurisdiction's required
  registration evidence must actually be satisfied."
  [{:keys [op subject]} st]
  (when (contains? #{:filing/draft :filing/submit} op)
    (let [e (store/engagement st subject)
          assessment (store/assessment-of st subject)]
      (when-not (and assessment
                     (facts/required-evidence-satisfied?
                      (:jurisdiction e) (:checklist assessment)))
        [{:rule :evidence-incomplete
          :detail "法域の必要書類(ROC登録/IRD TIN登録/Bid Security/CIDA登録/Blacklist未該当確認/BOI登録証明書等)が充足していない状態での提案"}]))))

(defn- slicing-violations
  "For `:filing/submit`, when the engagement declares `:sliced? true`,
  INDEPENDENTLY recompute whether the engagement's own declared slice
  group reconciles under the Procurement Guidelines - 2024 s.4.3 -- the
  flagship check this vertical adds. Entity-scope-gated (a no-op for an
  engagement never declared as a slice)."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)
          all (store/all-engagements st)]
      (when (registry/slicing-classification-evaded? e all)
        [{:rule :slicing-evades-classification
          :detail (str subject " のスライス(parent-procurement-id=" (:parent-procurement-id e)
                      ")はProcurement Guidelines - 2024 s.4.3の要件"
                      "(正当化事由の記録、またはスライス合算値の集計整合)を満たしておらず、"
                      "procurement authority level/procurement methodの回避に該当する可能性がある")}]))))

(defn- engagement-fee-mismatch-violations
  "For `:filing/submit`, INDEPENDENTLY recompute whether the
  engagement's own claimed fee equals base + months x rate."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when-not (registry/engagement-fee-matches-claim? e)
        [{:rule :engagement-fee-mismatch
          :detail (str subject " の申告手数料(" (:claimed-fee e)
                      ")が独立再計算値(" (registry/compute-engagement-fee e) ")と一致しない")}]))))

(defn- tin-registration-unverified-violations
  "For `:filing/submit`, when the engagement declares
  `:requires-tin-registration? true`, INDEPENDENTLY check
  `:tin-registered?` -- CONDITIONAL on the engagement's own ground
  truth. Grounded in the Inland Revenue Act No. 24 of 2017 ss.102/103
  Taxpayer Identification Number (TIN) registration duty owed to the
  Inland Revenue Department (IRD)."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when (and (true? (:requires-tin-registration? e))
                 (not (true? (:tin-registered? e))))
        [{:rule :tin-registration-unverified
          :detail (str subject " はInland Revenue Department(Inland Revenue Act No. 24 of 2017 ss.102/103)へのTIN登録確認を要するが未確認 -- 提出提案は進められない")}]))))

(defn- already-drafted-violations
  "For `:filing/draft`, refuses to draft the SAME engagement twice."
  [{:keys [op subject]} st]
  (when (= op :filing/draft)
    (when (store/engagement-already-drafted? st subject)
      [{:rule :already-drafted
        :detail (str subject " は既にドラフト済み")}])))

(defn- already-submitted-violations
  "For `:filing/submit`, refuses to submit the SAME engagement twice."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (when (store/engagement-already-submitted? st subject)
      [{:rule :already-submitted
        :detail (str subject " は既に提出済み")}])))

(defn check
  "Censors a MarketEntry-LLM proposal against the governor rules.
  Returns {:ok? bool :violations [..] :confidence c :escalate? bool
  :high-stakes? bool :hard? bool}."
  [request _context proposal st]
  (let [hard (into []
                   (concat (spec-basis-violations request proposal)
                           (evidence-incomplete-violations request st)
                           (slicing-violations request st)
                           (engagement-fee-mismatch-violations request st)
                           (tin-registration-unverified-violations request st)
                           (already-drafted-violations request st)
                           (already-submitted-violations request st)))
        conf (:confidence proposal 0.0)
        low? (< conf confidence-floor)
        stakes? (boolean (high-stakes (:stake proposal)))
        hard? (boolean (seq hard))]
    {:ok?          (and (not hard?) (not low?) (not stakes?))
     :violations   hard
     :confidence   conf
     :hard?        hard?
     :escalate?    (and (not hard?) (or low? stakes?))
     :high-stakes? stakes?}))

(defn hold-fact
  "The audit fact written when a proposal is rejected (HOLD)."
  [request context verdict]
  {:t          :governor-hold
   :op         (:op request)
   :actor      (:actor-id context)
   :subject    (:subject request)
   :disposition :hold
   :basis      (mapv :rule (:violations verdict))
   :violations (:violations verdict)
   :confidence (:confidence verdict)})
