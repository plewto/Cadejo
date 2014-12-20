(println "-->    masa genpatch")

(ns cadejo.instruments.masa.genpatch
  (:use [cadejo.util.trace])
  (:require [cadejo.util.math :as math])
  (:use [cadejo.instruments.masa.masa-constants])
  (:require [cadejo.instruments.masa.program :as program]))

(defn random-masa-program [& args]
  (let [harmonic-type (or (second (first args))
                          (rand-nth '[:b3 :b3 :b3 :b3
                                      :odd :odd :odd
                                      :harmonic :harmoinc
                                      :prime
                                      :semi-enharmonic
                                      :enharmonic]))
        rand-amp (fn [](int (rand 9)))
        rand-enharm (fn [](+ 0.5 (rand 8)))
        rand-harm (fn [n]
                    (let [f (math/coin 0.8 n (+ n (rand)))]
                      f))
        rand-one (fn [](* (math/coin 0.5 -1 +1)(rand)))
        rand-perc (fn [](math/coin 0.25 1 0))]
    (println ";; MASA gamut " harmonic-type)
    (program/masa
     :harmonics (cond (= harmonic-type :b3) b3
                      (= harmonic-type :odd) odd
                      (= harmonic-type :harmonic) harmonic
                      (= harmonic-type :prime) prime
                      (= harmonic-type :semi-enharmonic)
                      (sort [(rand-harm 0.5)(rand-harm 1)(rand-harm 2)
                             (rand-harm 3)(rand-harm 4)(rand-harm 5)
                             (rand-harm 6)(rand-harm 8)(rand-harm 9)])
                      :default
                      (sort [(rand-enharm)(rand-enharm)(rand-enharm)
                             (rand-enharm)(rand-enharm)(rand-enharm)
                             (rand-enharm)(rand-enharm)(rand-enharm)]))
     :registration [(rand-amp)(rand-amp)(rand-amp)
                    (rand-amp)(rand-amp)(rand-amp)
                    (rand-amp)(rand-amp)(rand-amp)]
     :pedals [0 0 0  0 0 0  0 0 0]
     :percussion [(rand-perc)(rand-perc)(rand-perc)
                  (rand-perc)(rand-perc)(rand-perc)
                  (rand-perc)(rand-perc)(rand-perc)]
     :amp 0.2
     ;:pedal-sens 0
     :decay (math/coin 0.75 (rand 0.33)(rand))
     :sustain (math/coin 0.75 (+ 0.75 (rand 0.25))(rand))
     :vrate (math/rand-range 3 8)
     :vsens (math/coin 0.75 (rand 0.01)(rand 0.1))
     :vdelay (rand 8)
     :scanner-delay 0.01
     :scanner-delay-mod (rand)
     :scanner-mod-rate (rand 7)
     :scanner-mod-spread (rand 4)
     :scanner-scan-rate (math/coin 0.7 (rand)(rand 7))
     :scanner-crossmix (rand)
     :scanner-mix (math/coin 0.5 (rand 0.5) 0)
     :room-size (rand)
     :reverb-damp (rand)
     :reverb-mix (math/coin 0.5 (rand) 0)) ))
