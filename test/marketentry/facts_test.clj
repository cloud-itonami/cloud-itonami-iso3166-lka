(ns marketentry.facts-test
  (:require [clojure.string :as str]
            [clojure.test :refer [deftest is testing]]
            [marketentry.facts :as facts]))

(deftest lka-has-spec-basis
  (let [sb (facts/spec-basis "LKA")]
    (is (some? sb))
    (is (string? (:provenance sb)))
    (is (seq (:required-evidence sb)))
    (is (some? (facts/corporate-number-spec-basis "LKA")))
    (is (some? (facts/business-registration-spec-basis "LKA")))
    (is (some? (facts/slicing-spec-basis "LKA")))))

(deftest lka-rep-spec-basis-is-honest-nil
  (testing "LKA's rep-spec-basis is nil -- this iteration found a real Sanctioned/Debarred/Blacklisted mechanism (Guidelines - 2024 ss.10.3/10.4) but did not independently fetch a live, queryable copy of that list this session, not a negative finding within primary text actually read"
    (is (nil? (facts/rep-spec-basis "LKA")))))

(deftest lka-business-registration-is-a-different-body-from-tax-and-procurement
  (testing "business/company registration (ROC) and tax registration (IRD) are administered by different authorities -- see namespace docstring"
    (let [reg (facts/business-registration-spec-basis "LKA")
          tax (facts/corporate-number-spec-basis "LKA")]
      (is (some? reg))
      (is (some? tax))
      (is (not= (:business-registration-owner-authority reg)
                (:corporate-number-owner-authority tax))))))

(deftest lka-slicing-is-the-flagship-spec-basis
  (testing "the Procurement Guidelines - 2024 s.4.3 anti-slicing provision is a real, government-published enactment -- not fabricated"
    (let [sl (facts/slicing-spec-basis "LKA")]
      (is (some? sl))
      (is (re-find #"s\.4\.3" (:slicing-legal-basis sl)))
      (is (re-find #"nprocom\.gov\.lk" (:slicing-provenance sl))))))

(deftest lka-owner-authority-is-not-the-police-commission
  (testing "the real National Procurement Commission is nprocom.gov.lk, never npc.gov.lk (the National Police Commission) -- see namespace docstring's research correction"
    (let [sb (facts/spec-basis "LKA")]
      (is (re-find #"nprocom\.gov\.lk" (:provenance sb)))
      (is (not (str/includes? (:provenance sb) "npc.gov.lk"))))))

(deftest unknown-jurisdiction-has-no-spec-basis
  (is (nil? (facts/spec-basis "ATL")))
  (is (nil? (facts/spec-basis "ZZZ")))
  (is (nil? (facts/business-registration-spec-basis "ATL")))
  (is (nil? (facts/slicing-spec-basis "ATL"))))

(deftest required-evidence-satisfied
  (let [sb (facts/spec-basis "LKA")
        all (:required-evidence sb)]
    (is (true? (facts/required-evidence-satisfied? "LKA" all)))
    (is (not (facts/required-evidence-satisfied? "LKA" (take 1 all))))
    (is (nil? (facts/required-evidence-satisfied? "ATL" all)))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["LKA" "USA" "ATL"])]
    (is (= 3 (:requested c)))
    (is (= 2 (:covered c)))
    (is (= ["ATL"] (:missing-jurisdictions c)))))
