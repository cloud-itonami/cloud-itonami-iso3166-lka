(ns statute.facts
  "General-law compliance catalog for the Democratic Socialist Republic
  of Sri Lanka (LKA) -- extends this repo's existing `marketentry.facts`
  (public-procurement market-entry only, narrow scope) with a second,
  orthogonal catalog of statutes a company operating in this jurisdiction
  must generally track for compliance. Mirrors cloud-itonami-iso3166-aze/
  -bih/-jpn/-deu/-bgr/-blr/-bol/-blz/-atg/-brb/-brn/-btn/-npl/-kgz's
  `statute.facts` (ADR-2607141700, cloud-itonami-compliance-fact-
  federation).

  Every entry below cites an OFFICIAL Sri Lankan government source,
  curl-verified 2026-07-23, and every citation reflects PRIMARY TEXT
  this iteration actually fetched (`curl` + `pdftotext -layout`) and read
  in full, not a secondary summary:

  - **The Companies Act, No. 07 of 2007** -- downloaded in full as a PDF
    directly from the Department of the Registrar of Companies' (ROC)
    own official site (`drc.gov.lk/en/wp-content/uploads/2018/04/
    Act-7-of-2007-English.pdf`) and read via `pdftotext -layout`. HIGH
    confidence, primary text: own title page 'COMPANIES ACT, No. 07 OF
    2007 [Certified on 20th March, 2007]'; own Part XVI ('Registrar-
    General of Companies and Registration'), s.471(1) 'There may be
    appointed -- (a) a person by name or by office, to be or to act as
    the Registrar-General of Companies'; own Definitions: '\"Registrar\"
    means, the Registrar-General of Companies or other officer
    performing under this Act, the duty of registration of companies';
    own s.4(1) 'any person or persons may apply to incorporate a
    company... by making an application for the same to the Registrar
    in the prescribed form'. This is the statute this repo's
    `marketentry.facts` `business-registration-spec-basis` cites for the
    Department of the Registrar of Companies' grounding. ROC's own site
    (`drc.gov.lk/en/`, 'About us') separately confirms this is the
    CURRENT Companies Act among its administered enactments ('namely,
    Companies Act No 7 of 2007, The Societies Ordinance No 16 of
    1891...'), and that ROC itself dates to 1938. This iteration also
    noted (but did not independently fetch the full text of) a
    'COMPANIES AMENDMENT ACT No. 12 OF 2025' listed on ROC's own
    downloads page (an honest, explicit gap: the core Part XVI/s.4/s.471
    citations above were independently verified against the 2007
    principal Act's own text, not the 2025 amendment).
  - **The Inland Revenue Act, No. 24 of 2017** -- downloaded in full as a
    PDF directly from the Inland Revenue Department's (IRD) own official
    site (`ird.gov.lk/en/publications/Acts_Income Tax_2017/
    IR_Act_No_24_2017_E.pdf`) and read via `pdftotext -layout`. HIGH
    confidence, primary text: own title page 'INLAND REVENUE ACT, No. 24
    OF 2017 [Certified on 24th of October, 2017]'; own Division II
    ('Taxpayer Registration and Taxpayer Identification Numbers'), s.102
    (1) 'Every person liable to furnish a return of income for a year of
    assessment, and who has not already registered, shall register with
    the Commissioner-General not later than thirty days after the end of
    the basis period for that year'; s.103(1) 'The Commissioner-General
    shall assign a unique TIN [Taxpayer Identification Number] to every
    taxpayer which shall be used in all correspondence relating to the
    administration of this Act'. This is the statute this repo's
    `marketentry.facts` `corporate-number-spec-basis` cites for TIN
    registration. IRD's own Acts index (fetched directly) lists numerous
    amendment Acts through 2026 (e.g. No. 11 of 2026); this iteration did
    NOT independently fetch every amendment's own text (an honest,
    explicit gap -- the s.102/s.103 registration/TIN mechanism itself was
    not observed to be repealed by any amendment title in that listing).
  - **The Board of Investment Law, No. 4 of 1978** (enacted as the
    Greater Colombo Economic Commission Law, No. 4 of 1978; renamed by
    the Greater Colombo Economic Commission (Amendment) Act, No. 49 of
    1992) -- both instruments downloaded directly from the Board of
    Investment of Sri Lanka's (BOI) own official site
    (`investsrilanka.com/wp-content/uploads/2025/06/
    BoiLaw-No-4_of_1978.pdf` and `.../BoiLaw_No-49_of_1992.pdf`). BOTH
    are scanned Gazette reproductions with NO PDF text layer (`pdftotext`
    returned zero output for each) -- this iteration rendered pages via
    `pdftoppm` and OCR'd them via `tesseract`, an explicitly
    LOWER-confidence transcription than a native text layer (visible OCR
    artifacts, e.g. 'Econo-MIC', 'porposes', are not reproduced verbatim
    in this catalog's `:statute/law-number` text). Own 1978 title page:
    'GREATER COLOMBO ECONOMIC COMMISSION LAW, No. 4 OF 1978... [Certified
    on 31st January, 1978]'; own s.2(1)-(2) 'There shall be established
    a Commission called the Greater Colombo Economic Commission... a
    body corporate having perpetual succession'; own s.3 objects include
    '(c) to encourage and promote foreign investment within the
    Republic'; own s.6(1) 'The Commission shall consist of five members
    to be appointed by the President, one of whom shall be appointed the
    Director-General'. The 1992 Amendment Act's own s.2 independently
    confirms the rename BOI's own 'Who we are' page claims: 'The long
    title to the Greater Colombo Economic Commission Law, No. 4 of 1978
    ... is hereby amended by the substitution, for the words \"Greater
    Colombo Economic Commission\" and \"the said Commission\", of the
    words \"Board of Investment of Sri Lanka\" and \"the said Board\",
    respectively'. This iteration did NOT independently fetch BOI's
    current sector/negative-list or FDI-approval-threshold material (an
    honest, explicit gap -- this catalog cites only the Law's own
    establishment/objects/composition text, not any specific equity
    percentage or sector-eligibility claim).

  A law not in this table has NO spec-basis, full stop; extend
  `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of statute entries. `:statute/url` + `:statute/law-number`
  are the citation the governor requires before any compliance-fact
  proposal referencing this law can commit."
  {"LKA"
   [{:statute/id "lka.companies-act-2007"
     :statute/title "Companies Act, No. 07 of 2007"
     :statute/jurisdiction "LKA"
     :statute/kind :law
     :statute/law-number "Part XVI ('Registrar-General of Companies and Registration'), s.471(1) 'There may be appointed... a person... to be or to act as the Registrar-General of Companies'; Definitions: '\"Registrar\" means, the Registrar-General of Companies or other officer performing under this Act, the duty of registration of companies'; s.4(1) (incorporation application 'to the Registrar in the prescribed form'); administered by the Department of the Registrar of Companies (established 1938)"
     :statute/url "https://www.drc.gov.lk/en/wp-content/uploads/2018/04/Act-7-of-2007-English.pdf"
     :statute/url-provenance :roc-official-site
     :statute/enacted-date "2007-03-20"
     :statute/retrieved-at "2026-07-23"
     :statute/topic #{:corporate-governance :incorporation}}
    {:statute/id "lka.inland-revenue-act-2017"
     :statute/title "Inland Revenue Act, No. 24 of 2017"
     :statute/jurisdiction "LKA"
     :statute/kind :law
     :statute/law-number "Division II ('Taxpayer Registration and Taxpayer Identification Numbers'), s.102(1) registration duty ('shall register with the Commissioner-General not later than thirty days after the end of the basis period'); s.103(1) 'The Commissioner-General shall assign a unique TIN to every taxpayer'; administered by the Inland Revenue Department, Commissioner-General of Inland Revenue"
     :statute/url "https://www.ird.gov.lk/en/publications/Acts_Income%20Tax_2017/IR_Act_No_24_2017_E.pdf"
     :statute/url-provenance :ird-official-site
     :statute/enacted-date "2017-10-24"
     :statute/retrieved-at "2026-07-23"
     :statute/topic #{:tax}}
    {:statute/id "lka.boi-law-4-1978"
     :statute/title "Board of Investment Law, No. 4 of 1978 (enacted as the Greater Colombo Economic Commission Law, No. 4 of 1978; renamed by the Greater Colombo Economic Commission (Amendment) Act, No. 49 of 1992)"
     :statute/jurisdiction "LKA"
     :statute/kind :law
     :statute/law-number "s.2(1)-(2) establishment of the Commission as a body corporate (renamed 'the Board' by Act No. 49 of 1992 s.2/s.3); s.3 objects, including '(c) to encourage and promote foreign investment within the Republic'; s.6(1) constitution (five members appointed by the President, one as Director-General, per the 1978 text, amended by Act No. 49 of 1992 s.4 to add an Additional Director-General); administered by the Board of Investment of Sri Lanka. OCR'd from a scanned Gazette reproduction (no native PDF text layer) -- see namespace docstring"
     :statute/url "https://investsrilanka.com/wp-content/uploads/2025/06/BoiLaw-No-4_of_1978.pdf"
     :statute/url-provenance :boi-official-site
     :statute/enacted-date "1978-01-31"
     :statute/retrieved-at "2026-07-23"
     :statute/topic #{:investment}}]})

(defn spec-basis
  "The jurisdiction's statute vector, or nil -- nil means NO spec-basis
  for that jurisdiction yet."
  [iso3]
  (get catalog iso3))

(defn coverage
  "Honest coverage report, same shape/discipline as `marketentry.facts/coverage`:
  never report a missing jurisdiction as covered."
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-lka statute.facts Wave 0 (ADR-2607141700): "
                 (count (get catalog "LKA")) " LKA statutes seeded with an "
                 "official citation. Extend "
                 "`statute.facts/catalog`, never fabricate a law-id or URL.")})))

(defn by-topic
  "Statutes for `iso3` tagged with `topic` (e.g. :tax, :investment)."
  [iso3 topic]
  (filterv #(contains? (:statute/topic %) topic) (spec-basis iso3)))
