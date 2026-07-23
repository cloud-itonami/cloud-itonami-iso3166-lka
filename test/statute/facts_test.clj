(ns statute.facts-test
  (:require [clojure.string :as str]
            [clojure.test :refer [deftest is]]
            [statute.facts :as facts]))

(deftest lka-has-spec-basis
  (let [sb (facts/spec-basis "LKA")]
    (is (= 3 (count sb)))
    (is (every? #(str/starts-with? (:statute/url %) "https://") sb))
    (is (every? :statute/law-number sb))))

(deftest unknown-jurisdiction-has-no-spec-basis
  (is (nil? (facts/spec-basis "ATL")))
  (is (nil? (facts/spec-basis "ZZZ"))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["LKA" "JPN" "ATL"])]
    (is (= 3 (:requested c)))
    (is (= 1 (:covered c)))
    (is (= ["ATL" "JPN"] (:missing-jurisdictions c)))))

(deftest by-topic-filters
  (is (= ["lka.inland-revenue-act-2017"]
         (mapv :statute/id (facts/by-topic "LKA" :tax))))
  (is (= ["lka.boi-law-4-1978"]
         (mapv :statute/id (facts/by-topic "LKA" :investment))))
  (is (empty? (facts/by-topic "LKA" :environment)))
  (is (empty? (facts/by-topic "ATL" :tax))))
