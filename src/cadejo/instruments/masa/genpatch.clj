(ns cadejo.instruments.masa.genpatch
  (:require [cadejo.util.math :as math])
  (:use [cadejo.instruments.masa.program]))
        
(defn random-masa-program [& {:keys [gamut]
                              :or {gamut nil}}]
  (let [harmonic-type (or gamut (rand-nth '[b3 b3 b3 b3
                                            odd odd odd
                                            harmonic
                                            prime 
                                            semi-enharmonic semi-enharmonic 
                                            enharmonic]))
        rand-amp (fn [](int (rand 9)))
        rand-enharm (fn [] (+ 0.5 (rand 8)))
        rand-harm (fn [n] (math/coin 0.7 n (+ n (rand))))
        rand-one (fn [] (* (math/coin 0.5 -1 +1)(rand)))]
    (println ";; MASA Harmonics gamut " harmonic-type)
    (masa 
     :harmonics (cond (= harmonic-type 'b3) b3
                      (= harmonic-type 'odd) odd
                      (= harmonic-type 'harmonic) harmonic
                      (= harmonic-type 'prime) prime
                      (= harmonic-type 'semi-enharmonic)
                      (sort [(rand-harm 1)(rand-harm 2)(rand-harm 3)
                             (rand-harm 4)(rand-harm 5)(rand-harm 6)
                             (rand-harm 7)(rand-harm 8)(rand-harm 9)])
                      :default
                      (sort [(rand-enharm)(rand-enharm)(rand-enharm)
                             (rand-enharm)(rand-enharm)(rand-enharm)
                             (rand-enharm)(rand-enharm)(rand-enharm)]))
     :registration [(rand-amp)(rand-amp)(rand-amp)
                    (rand-amp)(rand-amp)(rand-amp)
                    (rand-amp)(rand-amp)(rand-amp)]
     :pedals [0 0 0 0 0 0 0 0 0]
     :percussion [0 0 0 0 0 0 0 0 0]
     :amp 0.2
     :pedal-sens 0
     :decay 0.2
     :sustain 0.9
     :vrate (math/rand-range 3 8)
     :vsens (math/coin 0.25 (rand)(rand 0.01))
     :vdepth (math/coin 0.25 (rand) 0)
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
     :reverb-mix (math/coin 0.5 (rand) 0))))
