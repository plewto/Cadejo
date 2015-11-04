(println "    --> xolotl.util")
(ns xolotl.util
  (:require [clojure.string :as str])
  (:require [clojure.math.numeric-tower]))

(def abs
  clojure.math.numeric-tower/abs)

(def expt 
  clojure.math.numeric-tower/expt)

(defn int?
  ([obj]
   (and (number? obj)(= obj (int obj))))
  ([obj mn mx]
   (and (int? obj)(>= obj mn)(<= obj mx))))


;; Converts argument to canonical boolean
;; treats numeric 0 as false
;;
(defn ->bool [obj]
  (cond (and (int? obj) (zero? obj)) false
        obj true
        :else false))

(defn ->list [obj]
  (cond (list? obj) obj
        (= (type obj) clojure.lang.LazySeq) obj
        (vector? obj)(reverse (into () obj))
        :else (list obj)))

(defn member? [obj col]
  (some (fn [n](= n obj)) col))



;; Convert string to upper case and parse on white-space
;; Returns list
(defn tokenize [s]
  (filter (fn [q](not (= q "")))
          (str/split (str/upper-case (str s)) #"[ \n\t]")))


;; Convert string to decimal
;; String may have optional initial sign [-+] followed by one or
;; more decimal digits [0123456789].
;; If string is not a valid decimal, return nil
;;
(defn str->int [s]
  (try
    (Integer/parseInt s)
    (catch NumberFormatException ex
      nil)))

(defn str->float [s]
  (try
    (Float/parseFloat s)
    (catch NumberFormatException ex
      nil)))

;; CREDIT http://rosettacode.org/wiki/Count_occurrences_of_a_substring#Clojure
;;
(defn count-substring [txt sub]
  (count (re-seq (re-pattern sub) txt)))


(defn warning [& args]
  (doseq [a args]
    (println (format "WARNING: %s" a))))


(defn has-extension? [filename ext]
  "Predicate returns true if filename has extension ext.
   Do not include period in ext

   (has-extension? foo.bar bar) --> true
   (has-extension? foo.bar baz) --> false"
  (.endsWith filename (str "." ext)))

(defn append-extension [filename ext]
  "Appends extension ext to filename if it does not already 
   exists.
 
   (append-extension foo.bar bar) --> foo.bar
   (append-extension foo.bar baz) --> foo.bar.baz"
  (if (not (has-extension? filename ext))
    (str filename "." ext)
    filename))


(defn format-binary [n bits]
  (let [sb (StringBuilder. bits)
        weight* (atom 1)]
    (dotimes [i bits]
      (let [b (bit-and @weight* n)]
        (.append sb (if (zero? b) "_" "+"))
        (swap! weight* (fn [q](* q 2)))))
    (.toString sb)))
