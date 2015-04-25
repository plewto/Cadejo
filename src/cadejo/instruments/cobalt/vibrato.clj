(println "-->    cobalt vibrato")

(ns cadejo.instruments.cobalt.vibrato
  (:use [overtone.core]))

(defsynth Vibrato [vibrato-frequency 7.00
                   vibrato-sensitivity 0.10
                   vibrato-depth 0.00
                   vibrato<-pressure 0.00
                   cc1-bus 0
                   pressure-bus 0
                   vibrato-bus 0]
  (let [cc1 (in:kr cc1-bus)
        prss (in:kr pressure-bus)
        amp (* vibrato-sensitivity
               (+ vibrato-depth
                  (* prss vibrato<-pressure)
                  cc1))]
    (out:kr vibrato-bus (* amp (sin-osc:kr vibrato-frequency)))))
