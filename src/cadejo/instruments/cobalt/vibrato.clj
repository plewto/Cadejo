(println "-->    cobalt vibrato")

(ns cadejo.instruments.cobalt.vibrato
  (:use [overtone.core])
  (:require [cadejo.modules.qugen :as qu]))

(defsynth Vibrato [gate 0
                   vibrato-frequency 7.00
                   vibrato-sensitivity 0.10
                   vibrato-delay 1.00
                   vibrato-depth 0.00
                   vibrato<-pressure 0.00
                   cc1-bus 0
                   pressure-bus 0
                   vibrato-bus 0]
  (let [cc1 (in:kr cc1-bus)
        prss (in:kr pressure-bus)
        amp (* vibrato-sensitivity
               (+ (* vibrato-depth (lag2:kr gate (* 4 vibrato-delay)))
                  (* prss vibrato<-pressure)
                  cc1))]
    (out:kr vibrato-bus (* amp (sin-osc:kr vibrato-frequency)))))
