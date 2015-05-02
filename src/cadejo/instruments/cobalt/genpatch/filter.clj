(println "-->    cobalt genpatch filter")

(ns cadejo.instruments.cobalt.genpatch.filter
  (:use [cadejo.instruments.cobalt.program])
  (:require [cadejo.util.math :as math])
  (:require [cadejo.instruments.cobalt.constants :as con]))

(def coin math/coin)

(defn select-gross-filter-mode []
  (rand-nth (into [] (flatten [(repeat 3 :off)
                               (repeat 3 :low1)
                               (repeat 1 :low2)
                               (repeat 1 :band1)
                               (repeat 2 :band2)
                               (repeat 3 :low+band)]))))

(defn bypass-filter-mode [env]
  [(lp-filter :freq [con/max-filter-cutoff :track 0 :env 0 :prss 0 :cca 0 :ccb 0]
              :res [0 :cca 0 :ccb 0]
              :env env
              :mode -1)
   (bp-filter :offset 1)])


;; low filter freq with positive env modulation
;;
(defn lowpass-filter-mode1 [env]
  (let [track (int (rand-nth [0 0 0 0 1 2]))
        freq (int (+ con/min-filter-cutoff (coin 0.5 0 (rand (* 3/8 con/max-filter-cutoff)))))
        res (rand 0.9)
        env-mod (float (cond (and (zero? track)(< freq (* 1/4 con/max-filter-cutoff))) (+ 5/8 (rand 3/8)) 
                             (and (= track 1)(> freq (* 5/8 con/max-filter-cutoff))) (coin 0.5 0.0 (rand))
                             :default (rand)))
        mode (+ -1.0  (rand 0.5))
        offset (rand-nth [1 2 3 4 6 8])]
    (println (format ";; lowpass filter track %2d freq %5d res %4.2f env %+5.2f mode %+5.2f bp-offset %d"
                     track freq res env-mod mode offset))
    [(lp-filter :freq [freq :track track :env env-mod :prss 0.00 :cca 0.00 :ccb 0.00]
                :res [res :cca 0.0 :ccb 0.0]
                :env env
                :mode mode)
     (bp-filter :offset offset)]))

;; high lp filter freq with negative env modulation
;;
(defn lowpass-filter-mode2 [env]
  (let [track 0
        freq con/max-filter-cutoff
        res (rand 0.9)
        env-mod (* -1.0 (+ 0.5 (rand 0.5)))
        mode (+ -1.0  (rand 0.5))
        offset (rand-nth [1 2 3 4 6 8])]
    (println (format ";; lowpass filter track %2d freq %5d res %4.2f env %+5.2f mode %+5.2f bp-offset %d"
                     track freq res env-mod mode offset))
    [(lp-filter :freq [freq :track track :env env-mod :prss 0.00 :cca 0.00 :ccb 0.00]
                :res [res :cca 0.0 :ccb 0.0]
                :env env
                :mode mode)
     (bp-filter :offset offset)]))


;; Tracking band pass
(defn bandpass-filter-mode1 [env]
  (let [track (rand-nth [1 2 3 4 6 8])
        freq 0
        res (rand 0.5)
        env-mod 0
        mode (+ 0.5 (rand 0.5))
        offset 1]
    (println (format ";; Tracking bandpass filter track %2d res %4.2f mode %+5.2f"
                     track res mode))
    [(lp-filter :freq [freq :track track :env env-mod :prss 0.00 :cca 0.00 :ccb 0.00]
                :res [res :cca 0.0 :ccb 0.0]
                :env env
                :mode mode)
     (bp-filter :offset offset)]))

(defn low+band-filter-mode [env mode-bias]
  (let [track (rand-nth [1 2 3])
        freq 0
        res (rand 0.9)
        env-mod (rand)
        mode (+ mode-bias (rand 1/2))
        offset (float (rand-nth [1/2 2 3 4]))]
    (println (format ";; low+band filter track %2d env %4.2f res %4.2f mode %+5.2f bp-offset %s"
                     track env-mod res mode offset))
    [(lp-filter :freq [freq :track track :env env-mod :prss 0.00 :cca 0.00 :ccb 0.00]
                :res [res :cca 0.0 :ccb 0.0]
                :env env
                :mode mode)
     (bp-filter :offset offset)]))


(defn select-filter [fenv]
  (let [m (select-gross-filter-mode)]
    (println ";; Gross filter-mode " m)
    (cond (= m :off)(bypass-filter-mode fenv)
          (= m :low1)(lowpass-filter-mode1 fenv)
          (= m :low2)(lowpass-filter-mode2 fenv)
          (= m :band1)(bandpass-filter-mode1 fenv)
          (= m :band2)(low+band-filter-mode fenv 1/2)
          :default (low+band-filter-mode fenv -1/4) )))


        
        
              

