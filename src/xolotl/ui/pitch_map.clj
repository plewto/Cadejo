(ns xolotl.ui.pitch-map)

(def ^:private octaves (range -4 5))
(def ^:private roots {"C" 0, "D" 2, "E" 4, "F" 5, "G" 7, "A" 9, "B" 11})
(def ^:private modifiers {"F" -1, "" 0, "S" 1})
(def REST-LIMIT -49)           ; Any value <= REST-LIMIT is a rest.
(def REST -1000)               ; The canonical rest value.


;; Map symbolic pitch tokens to int MIDI key-number
;; Pitch symbol name format:   R0 or RM0
;;     where R -> Root key name "white key" : C D E F G A or B
;;           M -> optional modifier         : F = flat, S = sharp
;;           O -> octave number             : 0,1,2,3,4,5,6,7,8
;;                              
(def pitch-map (let [acc* (atom {})]
                 (doseq [oct octaves]
                   (let [oct-val (* 12 oct)]
                     (doseq [root (seq roots)]
                       (doseq [mod (seq modifiers)]
                         (let [sym (keyword (format "%s%s%d" (first root)(first mod) oct))
                               val (+ oct-val (second root)(second mod))]
                           (swap! acc* (fn [q](assoc q sym val))))))))
                 (swap! acc* (fn [q](assoc q :R REST)))
                 @acc*))


;; Map int MIDI key-numbers to to symbolic tokens
;; For accidentals "flat" keys are used.
;;
(def reverse-map (let [acc* (atom {})]
                   (doseq [oct octaves]
                     (let [oct-val (* 12 oct)]
                       (doseq [root (seq {"C" 0, "DF" 1, "D" 2, "EF" 3,
                                          "E" 4, "F" 5, "GF" 6, "G" 7,
                                          "AF" 8, "A" 9, "BF" 10, "B" 11})]
                         (let [sym (keyword (format "%s%d" (first root) oct))
                               val (+ oct-val (second root))]
                           (swap! acc* (fn [q](assoc q val sym)))))))
                   (swap! acc* (fn [q](assoc q REST :R)))
                   @acc*))
                         
;; Convert int to pitch token
;;
(defn int->pitch [n]
  (if (vector? n)
    (let [sb (StringBuilder. (+ 3 (count n)))]
      (.append sb "[")
      (doseq [m n]
        (.append sb (format "%s " (int->pitch m))))
      (.append sb "] ")
      (.toString sb))
    (if (<= n REST-LIMIT)
      "R"
      (let [token (get reverse-map n)]
        (if token
          (name token)
          (int n))))))
