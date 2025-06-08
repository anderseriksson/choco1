(ns clojure-choco-solver.util)

(defn assign-ids
  "Takes a sequential collection and returns a map of {id value} with ids from 0 to n.
   Throws an exception if input items are not unique."
  [coll]
  (let [v (vec coll)]
    (when (not= (count v) (count (set v)))
      (throw (ex-info "All items in the collection must be unique." {:input coll})))
    (into {} (map-indexed (fn [idx val] [idx val]) v))))




(defn column-from-java-array
  "Given a 2D Java array arr and index n, returns a vector of the nth element from each row."
  [arr n]
  (mapv #(aget ^objects arr % n) (range (alength arr))))

(defn rpad
  "Right pad string s to length n with spaces."
  [s n]
  (format (str "%-" n "s") s))
