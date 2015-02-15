
(ns cadejo.util.col
  "Provides collection related utility functions.")

(defn alist->map [alist]
  "Converts assoclist (:key1 value1 :key2 value2 ...) to Clojure map
   :ISSUE: Is there a more idiomatic Clojure way of doing this?"
  (let [acc* (atom {})]
    (doseq [i (range 0 (count alist) 2)]
      (swap! acc* (fn [n](assoc n (nth alist i)(nth alist (inc i))))))
    @acc*))

(defn map->alist [map]
  "Converts map to assoclist, order is not guaranteed
   {:key1 value1 :key2 value2 ...} --> (:key1 value1 key2 value2 ...)"
  (reverse (into '()(flatten (seq map)))))

(defn alist->keys [alist]
  "Returns list of assoclist keys
   (:key1 value1 :key2 value2 ...) --> (:key1 key2 ...)"
  (let [acc* (atom [])]
    (dotimes [i (/ (count alist) 2)]
      (swap! acc* (fn [n] (conj n (nth alist (* 2 i))))))
    @acc*))

(defn alist->values [alist]
  "Returns list of assoclist values
   (:key1 value1 :key2 value2 ...) --> (value1 value2 ...)"
  (let [acc* (atom [])]
    (dotimes [i (/ (count alist) 2)]
      (swap! acc* (fn [n] (conj n (nth alist (inc (* 2 i)))))))
    @acc*))

(defn member? [obj col]
  "Predicate true if obj is = to some element of collection."
  (some (fn [n](= n obj)) col))

(defn not-member? [obj col]
  "Predicate true if obj is not = to any element of collection."
  (not (member? obj col)))


(defn zip [a b]
  "Zip two list/vectors  
   (zip '[a b c] [1 2 3]) --> [[a 1][b 2][c 3]]"
  (map vector a b))

(defn third [col](nth col 2))
(defn fourth [col](nth col 3))
