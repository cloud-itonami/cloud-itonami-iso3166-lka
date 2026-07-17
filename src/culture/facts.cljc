(ns culture.facts
  "Country-level regional-culture catalog for Sri Lanka (LKA) -- national
  dishes, protected products, beverages, crafts, festivals and heritage
  sites, per ADR-2607171400 addendum 2 (cloud-itonami-municipality-
  culture-catalog Wave 1, in com-junkawasaki/root). Sibling namespace to
  `marketentry.facts` / `statute.facts` (ADR-2607141700); city-level
  counterparts live in the cloud-itonami-municipality-* repos.

  Catalog is keyed by UPPERCASE ISO3 (mirrors `statute.facts`); entries
  carry no :culture/municipality (that attribute is city-level only).

  Every entry cites a source URL that was actually fetched and read on
  :culture/retrieved-at -- never fabricated. Summaries state only what the
  cited source confirms. An item not in this table has NO spec-basis, full
  stop; extend `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of culture entries."
  {"LKA"
   [{:culture/id "lka.dish.kottu"
     :culture/name "Kottu"
     :culture/country "LKA"
     :culture/kind :dish
     :culture/summary "Sri Lankan Tamil dish of chopped roti stir-fried with meat curry, egg, onions and chillies; originated as affordable street food in the Tamil regions of Batticaloa and Trincomalee during the 1960s-1970s."
     :culture/url "https://en.wikipedia.org/wiki/Kottu"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "lka.dish.appam"
     :culture/name "Hoppers"
     :culture/name-local "Appa"
     :culture/country "LKA"
     :culture/kind :dish
     :culture/summary "Fermented rice-and-coconut-milk pancake popular in South India and Sri Lanka, where it is typically known as hoppers."
     :culture/url "https://en.wikipedia.org/wiki/Appam"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "lka.dish.lamprais"
     :culture/name "Lamprais"
     :culture/country "LKA"
     :culture/kind :dish
     :culture/summary "Sri Lankan dish introduced by the country's Dutch Burgher population, with roots in the Indonesian lemper dish adapted during the Dutch colonial period (1658-1796)."
     :culture/url "https://en.wikipedia.org/wiki/Lamprais"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "lka.beverage.ceylon-tea"
     :culture/name "Ceylon tea"
     :culture/country "LKA"
     :culture/kind :beverage
     :culture/summary "Brand of tea produced in Sri Lanka and historic term describing black tea from the island."
     :culture/url "https://en.wikipedia.org/wiki/Ceylon_tea"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "lka.product.ceylon-cinnamon"
     :culture/name "Ceylon cinnamon"
     :culture/country "LKA"
     :culture/kind :product
     :culture/summary "Cinnamomum verum (\"true cinnamon\"), a small evergreen tree native to Sri Lanka; the island produces 80-90% of the world's supply."
     :culture/url "https://en.wikipedia.org/wiki/Ceylon_cinnamon"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "lka.craft.ambalangoda-masks"
     :culture/name "Ambalangoda mask carving"
     :culture/country "LKA"
     :culture/kind :craft
     :culture/summary "Hand-carved and hand-painted traditional demon (Raksha), folktale (Kolam) and devil-dance (Sanni) wooden masks, centred on the coastal town of Ambalangoda in Sri Lanka's Southern Province."
     :culture/url "https://en.wikipedia.org/wiki/Ambalangoda"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "lka.festival.esala-perahera"
     :culture/name "Esala Perahera"
     :culture/country "LKA"
     :culture/kind :festival
     :culture/summary "Annual religious and cultural festival held in July-August in Kandy, Sri Lanka, honoring the Sacred Tooth Relic of Buddha through processions of elephants, traditional dancers and musicians."
     :culture/url "https://en.wikipedia.org/wiki/Esala_Perahera"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "lka.heritage.sigiriya"
     :culture/name "Sigiriya"
     :culture/name-local "Sinhagiri"
     :culture/country "LKA"
     :culture/kind :heritage
     :culture/summary "Ancient rock fortress in the Central Province of Sri Lanka, designated a UNESCO World Heritage Site in 1982 as an example of 5th-century urban planning."
     :culture/url "https://en.wikipedia.org/wiki/Sigiriya"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "lka.heritage.galle-fort"
     :culture/name "Galle Fort"
     :culture/country "LKA"
     :culture/kind :heritage
     :culture/summary "Fortified urban ensemble in Sri Lanka, inscribed as a UNESCO World Heritage Site in 1988 for illustrating the interaction of European architecture and South Asian traditions from the 16th to 19th centuries."
     :culture/url "https://en.wikipedia.org/wiki/Galle_Fort"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}]})

(defn spec-basis [iso3] (get catalog iso3))

(defn coverage
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-lka culture catalog "
                 "(ADR-2607171400 addendum 2, Wave 1): " (count (get catalog "LKA"))
                 " LKA entries, each with a fetched-and-read citation. "
                 "Extend `culture.facts/catalog`, never fabricate an id/url.")})))

(defn by-kind [iso3 kind]
  (filterv #(= (:culture/kind %) kind) (spec-basis iso3)))
