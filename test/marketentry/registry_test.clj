(ns marketentry.registry-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.registry :as registry]))

(deftest engagement-fee-recompute
  (let [e {:base-fee 800000 :monthly-rate 50000 :monitoring-months 6 :claimed-fee 1100000.0}]
    (is (== 1100000.0 (registry/compute-engagement-fee e)))
    (is (true? (registry/engagement-fee-matches-claim? e))))
  (let [bad {:base-fee 800000 :monthly-rate 50000 :monitoring-months 6 :claimed-fee 1500000.0}]
    (is (false? (registry/engagement-fee-matches-claim? bad)))))

(deftest register-draft-and-submit
  (let [d (registry/register-draft "eng-1" "LKA" 0)
        s (registry/register-submit "eng-1" "LKA" 0)]
    (is (= "LKA-DFT-000000" (get d "draft_number")))
    (is (= "LKA-SUB-000000" (get s "submit_number")))
    (is (nil? (get-in d ["certificate" "proof"])))
    (is (= "draft-unsigned" (get-in s ["certificate" "status"])))))

(deftest register-requires-ids
  (is (thrown? Exception (registry/register-draft "" "LKA" 0)))
  (is (thrown? Exception (registry/register-submit "eng-1" "" 0))))

(deftest slicing-lacks-justification-is-a-violation
  (testing "Procurement Guidelines - 2024 s.4.3(a) -- no slicing-justification on file -> evaded, even when the sole declared slice's own value trivially equals its own declared aggregate"
    (is (true? (registry/slicing-classification-evaded?
                {:sliced? true :parent-procurement-id "mpp-004"
                 :declared-tce 3000000.0 :aggregate-tce 3000000.0
                 :slicing-justification nil}
                [{:id "eng-4" :sliced? true :parent-procurement-id "mpp-004"
                  :declared-tce 3000000.0 :aggregate-tce 3000000.0
                  :slicing-justification nil}])))))

(deftest slicing-sum-mismatch-is-a-violation
  (testing "Procurement Guidelines - 2024 s.4.3(c) -- the sole slice on file for this parent-procurement-id declares far less than the group's own declared aggregate-tce, even with a justification on file"
    (is (true? (registry/slicing-classification-evaded?
                {:sliced? true :parent-procurement-id "mpp-005"
                 :declared-tce 500000.0 :aggregate-tce 50000000.0
                 :slicing-justification "small-contractor lot"}
                [{:id "eng-5" :sliced? true :parent-procurement-id "mpp-005"
                  :declared-tce 500000.0 :aggregate-tce 50000000.0
                  :slicing-justification "small-contractor lot"}])))))

(deftest properly-justified-reconciled-slices-are-not-evaded
  (testing "two sibling slices of the SAME parent-procurement-id, both justified, whose declared-tce sums to the group's own declared aggregate-tce -> not evaded"
    (let [siblings [{:id "eng-7" :sliced? true :parent-procurement-id "mpp-007"
                     :declared-tce 3000000.0 :aggregate-tce 6000000.0
                     :slicing-justification "Lot A/Lot B split by nature of goods"}
                    {:id "eng-8" :sliced? true :parent-procurement-id "mpp-007"
                     :declared-tce 3000000.0 :aggregate-tce 6000000.0
                     :slicing-justification "Lot A/Lot B split by nature of goods"}]]
      (is (false? (registry/slicing-classification-evaded? (first siblings) siblings)))
      (is (false? (registry/slicing-classification-evaded? (second siblings) siblings))))))

(deftest non-sliced-engagement-is-never-gated
  (testing "entity-scope-gated: a no-op (false) unless :sliced? is true -- an engagement never declared as a slice has no s.4.3 obligation at all"
    (is (false? (registry/slicing-classification-evaded?
                 {:sliced? false :parent-procurement-id nil
                  :declared-tce nil :aggregate-tce nil :slicing-justification nil}
                 [])))
    (is (false? (registry/slicing-classification-evaded?
                 {:parent-procurement-id nil} [])))))
