(ns cadejo.instruments.cobalt.randprog.registration
  (:require [cadejo.util.math :as math])
  (:require [cadejo.instruments.cobalt.randprog.config :as config]))

(def ^:private coin math/coin)


;; Use frequency gamut to select index of partial to have peak amplitude.
;; Gamut frequencies below 0.4 and above 8.0 are not considered.
;; Frequencies between 1 and 4 have greates chance of being selected.
;; Frequencies betwwen 0.4 and 1.0 and frequencies between 4 and 8
;; have lower chance of selection.
;; Returns single integer index.
;;
(defn pick-peak-partial-index [gamut]
  (let [acc* (atom [])]
    (dotimes [i (count gamut)]
      (let [f (nth gamut i)
            x (cond (and (> f 0.4)(< f 1.0)) 2
                    (and (>= f 1) (< f 4.0)) 6
                    (and (>= f 4) (< f 8.0)) 1
                    :default 0)]
        (swap! acc* (fn [q](conj q (repeat x i))))))
    (rand-nth (flatten @acc*))))


(defn rand-registration [mn mx p-zero]
  (let [acc* (atom [])
        diff (- mx mn)]
    (dotimes [i 10]
      (swap! acc* (fn [q](conj q (coin p-zero  
                                       0.0 
                                       (+ mn (rand diff)))))))
    @acc*))

(defn linear-registration [gamut p-zero p-second-peak]
  (let [ppi (pick-peak-partial-index gamut)
        ppf (float (nth gamut ppi))
        acc* (atom [])]
    (doseq [f gamut]
      (let [ratio (float (/ (min ppf f)(max ppf f)))]
        (swap! acc* (fn [q](conj q (if (= ratio 1.0) 
                                     1.0
                                     (coin p-zero 0.0 
                                           (coin p-second-peak 
                                                 (+ 0.75 (rand 0.25))
                                                 ratio))))))))
    @acc*))

(defn exp-registration [gamut p-zero p-second-peak]
  (let [ppi (pick-peak-partial-index gamut)
        ppf (float (nth gamut ppi))
        acc* (atom [])]
    (doseq [f gamut]
      (let [ratio (float (/ (min ppf f)(max ppf f)))]
        (swap! acc* (fn [q](conj q (if (= ratio 1.0) 
                                     1.0
                                     (coin p-zero 0.0 
                                           (coin p-second-peak 
                                                 (+ 0.75 (rand 0.25))
                                                 (* ratio ratio)))))))))
    @acc*))


(defn build-registration-selection-list []
  (flatten [(repeat (* 10 @config/p-registration-linear*) :linear)
            (repeat (* 10 @config/p-registration-exp*) :exp)
            (repeat (* 10 @config/p-registration-rand*) :rand)]))

(defn select-registration [gamut]
  (let [mode (if @config/test-mode* :test
                 (rand-nth (build-registration-selection-list)))
        p-zero @config/p-registration-zero*
        p-peak @config/p-registration-peak*]
    (if @config/verbose*
      (println (format ";; Registration mode %s   p-zero = %5.2f  p-peak = %5.2f"
                       mode p-zero p-peak)))
    (cond (= mode :test) @config/test-registration*
          (= mode :linear)(linear-registration gamut p-zero p-peak)
          (= mode :exp)(exp-registration gamut p-zero p-peak)
          :default (rand-registration 0.1 1.0 p-zero))))
