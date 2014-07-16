
(ns cadejo.scale.eqtemp
  (:require [cadejo.scale.table])
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.util.math :as math])
  (:require [cadejo.scale.scale-utilities :as scale-util]))

(defn eqtemp-table
  "Returns new tuning table using equal-tempered intonation.
   n       - int, number of noes per octave
   a440    - float, frequency of A440, default 440.0
   octave  - float, ratio used for an octave, default 2.0"
  ([n a440 octave]
     (let [tt (cadejo.scale.table/create-tuning-table)
           c0 (scale-util/a69->c0 a440 n octave)
           common-ratio (math/expt octave (/ 1.0 n))]
       ;; Build first octave
       (let [freq* (atom c0)]
         (dotimes [k n]
           (.set-key-frequency! tt k @freq*)
           (swap! freq* (fn [n](* n common-ratio)))))
       ;; Copy remaining octaves
       (doseq [start (range 0 cadejo.scale.table/table-length n)]
         (scale-util/copy-with-transpose! tt start (+ start n)(+ start n) 2))
       (.put-property! tt :name (keyword (format "eq-%d" n)))
       (.put-property! tt :intonation "equal tempered")
       (.put-property! tt :octave-size octave)
       (.put-property! tt :notes-per-octave n)
       tt))
  ([n a440]
     (eqtemp-table n a440 2.0))
  ([n]
     (eqtemp-table n 440.0)))

(def default-table (eqtemp-table 12))

